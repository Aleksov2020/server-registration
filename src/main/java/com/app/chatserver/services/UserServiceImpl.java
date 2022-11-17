package com.app.chatserver.services;

import java.awt.image.BufferedImage;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.app.chatserver.dto.*;
import com.app.chatserver.enums.ServerErrors;
import com.app.chatserver.exceptions.SmsException;
import com.app.chatserver.exceptions.TokenException;
import com.app.chatserver.model.Avatar;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.chatserver.exceptions.RegistrationException;
import com.app.chatserver.exceptions.UserException;
import com.app.chatserver.model.User;
import com.app.chatserver.repository.UserRepository;

import javax.imageio.ImageIO;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
	private final JwtTokenServiceImpl jwtTokenServiceImpl;
	private final TokenProviderServiceImpl tokenProviderServiceImpl;
	private final RefreshTokenServiceImpl refreshTokenServiceImpl;
	private final SmsService smsService;
	private final AvatarService avatarService;
	private final UserRepository userRepository;
	private final FileService fileService;
	@Value("${hay.pathToSave}")
	private String pathToSave;

	public UserServiceImpl(JwtTokenServiceImpl jwtTokenServiceImpl,
						   TokenProviderServiceImpl tokenProviderServiceImpl,
						   RefreshTokenServiceImpl refreshTokenServiceImpl,
						   SmsService smsService,
						   AvatarService avatarService,
						   FileService fileService,
						   UserRepository userRepository) {
		this.jwtTokenServiceImpl = jwtTokenServiceImpl;
		this.tokenProviderServiceImpl = tokenProviderServiceImpl;
		this.refreshTokenServiceImpl = refreshTokenServiceImpl;
		this.smsService = smsService;
		this.avatarService = avatarService;
		this.userRepository = userRepository;
	   this.fileService = fileService;
	}
	@Override
	public User read(int id) {
		return userRepository.getById(id);
	}
	@Override
	public boolean update(User user, int id) {
		if (userRepository.existsById(id)) {
            user.setId(id);
            userRepository.save(user);
            return true;
        }
		return false;
	}
	@Override
	public boolean delete(int id) {
        if (userRepository.existsById(id)) {
        	userRepository.deleteById(id);
            return true;
        }
        return false;
	}
	@Override
	public Optional<User> findUserByUserName(String userName) {
		if (userRepository.existsUserByUserName(userName)) {
			return Optional.of(userRepository.findUserByUserName(userName));
		} else {
			return Optional.empty();
		}

	}

	@Override
	public User changeUserName(String newUserName, Integer userId) {
		if (!checkUserNameReg(newUserName)) {
			throw new UserException(ServerErrors.USER_NAME_INCORRECT.name());
		}
		if (userRepository.existsUserByUserName(newUserName)) {
			throw new RegistrationException(ERROR_USERNAME_ALREADY_EXIST);
		}

		User u = userRepository.findById(userId).orElseThrow(
				() -> new UserException(ServerErrors.USER_NOT_FOUND.name())
		);

		u.setUserName(
				newUserName
		);
		return userRepository.save(u);
	}

	@Override
	public Optional<User> findUserByPhone(String phone) {
		if (userRepository.existsUserByPhone(phone)){
			return Optional.of(userRepository.findUserByPhone(phone));
		} else {
			return Optional.empty();
		}
	}

	public boolean checkUserNameReg(String userName) {
		return (userName.matches("[\\w\\!\\-\\.]*") && (userName.length() < 100) && (userName.length() >= 3));
	}

	public boolean checkUserLastNameReg(String userLastName) {
		return (userLastName.matches("[a-zA-Zа-яА-Я]*") && (userLastName.length() < 100) && (userLastName.length() >= 3));
	}
	public boolean checkSmsCodeReg(String smsCode) {
		return (smsCode.matches("[\\d]*") && (smsCode.length() == 6));
	}

	public boolean checkUserPhoneReg(String userPhone) {
		return (userPhone.matches("[\\d]*") && (userPhone.length() == 11));
	}
	public void checkUserBirthDate(String birthDate) {
		if (!birthDate.matches("[\\d\\-]*")) {
			throw new UserException(ERROR_USER_BIRTH_DATE_INCORRECT);
		}
		String[] birthDateList = birthDate.split("-");
		if (birthDateList.length != 3) {
			throw new UserException(ERROR_USER_BIRTH_DATE_INCORRECT);
		}
		if ((Integer.valueOf(birthDateList[0]) > 31) || (Integer.valueOf(birthDateList[0]) < 0)) {
			throw new UserException(ERROR_USER_BIRTH_DATE_INCORRECT);
		}
		if ((Integer.valueOf(birthDateList[1]) > 12) || (Integer.valueOf(birthDateList[1]) < 0)) {
			throw new UserException(ERROR_USER_BIRTH_DATE_INCORRECT);
		}
		if ((Integer.valueOf(birthDateList[2]) < 1850) || ((Integer.valueOf(birthDateList[2])) > 2022)) {
			throw new UserException(ERROR_USER_BIRTH_DATE_INCORRECT);
		}
	}


	@Override
	public ResponseTokenProvider createTokenAndSendSmsCodeToUser(String userPhone, String userName) throws NoSuchAlgorithmException, InvalidKeyException {
		if (!checkUserPhoneReg(userPhone)) {
			throw new RegistrationException(ERROR_PHONE_NUMBER_INCORRECT);
		}
		if (userRepository.existsUserByPhone(userPhone)) {
			throw new RegistrationException(ERROR_PHONE_ALREADY_EXIST);
		}
		if (!checkUserNameReg(userName)){
			throw new RegistrationException(ERROR_USER_NAME_INCORRECT);
		}
		if (userRepository.existsUserByUserName(userName)) {
			throw new RegistrationException(ERROR_USERNAME_ALREADY_EXIST);
		}

		String tokenProvider = tokenProviderServiceImpl.generateTokenProvider(userPhone, userName);
		log.info("Sms code = " + smsService.generateSmsCode(jwtTokenServiceImpl.getSignature(tokenProvider)));
		log.info("Send token provider = " + tokenProvider + " for user with phone = " + userPhone + " and name = " + userName);

		return new ResponseTokenProvider(tokenProvider);
	}

	@Override
	public ResponseTokenProvider createTokenAndSendAuthSmsCodeToUser(String userPhone) throws NoSuchAlgorithmException, InvalidKeyException {
		if (!checkUserPhoneReg(userPhone)) {
			throw new RegistrationException(ERROR_PHONE_NUMBER_INCORRECT);
		}
		if (!userRepository.existsUserByPhone(userPhone)) {
			throw new RegistrationException(ERROR_USER_NOT_REGISTRED_YET);
		}
		User u = this.userRepository.findUserByPhone(userPhone);
		String tokenProvider = tokenProviderServiceImpl.generateTokenProvider(u.getPhone(), u.getUserName());
		//sService.sendSmsCode(phone);  							uncomment if u want send sms to user
		log.info("Sms code = " + smsService.generateSmsCode(jwtTokenServiceImpl.getSignature(tokenProvider)));
		log.info("Send token provider = " + tokenProvider + " for user with phone = " + userPhone);
		return new ResponseTokenProvider(tokenProvider);
	}

	@Override
	public String updateAdditionalInfo( String lastName,
									    String name,
									    String birthDate,
									    String token,
									    MultipartFile image) throws ParseException {
		if (!jwtTokenServiceImpl.validateToken(token)) {
			throw new TokenException(ERROR_TOKEN_IS_NOT_VALID);
		}
		User userBeforeSave = new User();
		if (lastName != null){
			if (!checkUserLastNameReg(lastName)) {
				throw new UserException(ERROR_USER_LAST_NAME_INCORRECT);
			}
			userBeforeSave.setLastName(lastName);
		}
		if (name != null) {
			if (!checkUserLastNameReg(name)) {
				throw new UserException(ERROR_NAME_INCORRECT);
			}
			userBeforeSave.setFirstName(name);
		}
		if (birthDate != null) {
			checkUserBirthDate(birthDate);
			SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
			userBeforeSave.setDateBirth(formatter.parse(birthDate));
		}
		User user = read((Integer) jwtTokenServiceImpl.getAllClaimsFromToken(token).get("id"));

		user.setLastName(userBeforeSave.getLastName());
		user.setFirstName(userBeforeSave.getFirstName());
		user.setDateBirth(userBeforeSave.getDateBirth());

		//TODO thumbnuils 80px - 80px
		Avatar avatar = new Avatar();
		try {
			if (image != null) {
				File convertFile = new File(pathToSave + user.getUserName() + "/img/" + "avatar.jpg");
				convertFile.createNewFile();
				FileOutputStream fout = new FileOutputStream(convertFile);
				fout.write(image.getBytes());
				fout.close();
				avatar.setSize(image.getSize());
				avatar.setPath(pathToSave + user.getUserName() + "/img/" + "avatar.jpg");
			}
			avatarService.saveAvatar(avatar);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		user.setAvatar(avatar);
		user.setIsActive(true);
		user.setDateCreate(new Date(System.currentTimeMillis()));
		user.setDateLastUpdate(new Date(System.currentTimeMillis()));
		user.setDateLastEnter(new Date(System.currentTimeMillis()));
		user.setIsBanned(false);
		user.setIsReported(false);
		this.update(user, (Integer) jwtTokenServiceImpl.getAllClaimsFromToken(token).get("id"));
		return MESSAGE_INFORMATION_WAS_ADDED;
	}

	@Override
	public ResponseUser serializeUserToSendSearch(User u) throws IOException {
		return new ResponseUser(
				new ResponseMedia(
					fileService.imageToBase64(u.getAvatar().getPath())
				),
				u.getUserName(),
				u.getFirstName(),
				u.getMiddleName()
		);
	}

	@Override
	public ResponseUserList searchUsers(String userName, Integer page, Integer pageSize) throws IOException {
		Pageable paging = PageRequest.of(page, pageSize, Sort.by("id").descending());
		List<User> userList = userRepository.findUserByUserNameStartsWith(userName, paging);
		List<ResponseUser> responseUserList = new ArrayList<>();

		for (User u : userList) {
			responseUserList.add(
					new ResponseUser(
							new ResponseMedia(
									fileService.imageToBase64(u.getAvatar().getPath())
							),
							u.getUserName(),
							u.getFirstName(),
							u.getMiddleName()
					)
			);
		}

		return new ResponseUserList(
				responseUserList
		);
	}

	@Override
	public void changeUserAvatar(String newImageBase64, Integer userId) throws IOException {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new UserException(ServerErrors.USER_NOT_FOUND.name())
		);

		String base64Image = newImageBase64.split(",")[1];
		byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

		ImageIO.write(
				img,
				"jpg",
				new File(pathToSave + user.getUserName() + "/img/" + "avatar.jpg")
		);

		Avatar avatar = user.getAvatar();
		avatar.setSize(0l);
		avatar.setPath(pathToSave + user.getUserName() + "/img/" + "avatar.jpg");

		avatarService.saveAvatar(avatar);

		user.setDateLastUpdate(new Date(System.currentTimeMillis()));
	}

	public ResponseAuthComplete validateSmsCodeAndRegisterUser(String tokenProvider, String code) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		tokenProviderServiceImpl.validateToken(tokenProvider);
		if (!checkSmsCodeReg(code)) {
			throw new SmsException(ERROR_CODE_IS_INVALID);
		}
		//TODO CHANGE IT !
		//if (!smsService.generateSmsCode(jwtTokenServiceImpl.getSignature(tokenProvider)).equals("code")) {
		//	throw new SmsException(ERROR_CODE_IS_NOT_EQUALS);
		//}

		Claims claims = tokenProviderServiceImpl.getAllClaimsFromToken(tokenProvider);

		String userName = (String) claims.get("name");
		String userPhone = (String) claims.get("phone");

		if (userRepository.existsUserByUserName(userName)) {
			throw new RegistrationException(ERROR_USERNAME_ALREADY_EXIST);
		}
		if (userRepository.existsUserByPhone(userPhone)) {
			throw new RegistrationException(ERROR_PHONE_ALREADY_EXIST);
		}

		User u = new User();
		u.setUserName(userName);
		u.setPhone(userPhone);
		u.setIsActive(true);
		u.setDateCreate(new Date(System.currentTimeMillis()));
		u.setDateLastUpdate(new Date(System.currentTimeMillis()));
		u.setDateLastEnter(new Date(System.currentTimeMillis()));
		u.setIsBanned(false);
		u.setIsReported(false);
		u.setSubscribedCounter(0);
		u.setSubscriberCounter(0);
		u.setPostsCounter(0);

		if (!fileService.checkDirectories(u.getUserName())) {
			fileService.createUserFolders(u.getUserName());
		}

		Avatar avatar = new Avatar();
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(javax.xml.bind.DatatypeConverter.parseBase64Binary(base64array[(int) (Math.random() * 65)])));
			avatar.setPath(pathToSave + u.getUserName() + "/img/" + "avatar.jpg");
			fileService.saveAvatar(
					img,
					pathToSave + u.getUserName() + "/img/" + "avatar.jpg"
			);
			avatarService.saveAvatar(avatar);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		u.setAvatar(avatar);
		this.userRepository.save(u);

		String jwtAuthToken = this.jwtTokenServiceImpl.createJwtAuthTokenByUserId(u.getId());
		String refreshToken = this.refreshTokenServiceImpl.getRefreshToken(findUserByUserName(userName).get()).getToken();

		log.info("User with id = " + u.getId() + " receive tokens: \n" + "refreshToken: " + refreshToken + "\n accessToken: " + jwtAuthToken);

		return new ResponseAuthComplete(jwtAuthToken, refreshToken);
	}

	public ResponseAuthComplete validateSmsCodeAndLoginUser(String tokenProvider, String code) throws NoSuchAlgorithmException, InvalidKeyException {
		tokenProviderServiceImpl.validateToken(tokenProvider);
		if (!checkSmsCodeReg(code)) {
			throw new SmsException(ERROR_CODE_IS_INVALID);
		}
		if (!smsService.generateSmsCode(jwtTokenServiceImpl.getSignature(tokenProvider)).equals(code)) {
			throw new SmsException(ERROR_CODE_IS_NOT_EQUALS);
		}
		Claims claims = tokenProviderServiceImpl.getAllClaimsFromToken(tokenProvider);

		String userPhone = (String) claims.get("phone");

		String jwtAuthToken = this.jwtTokenServiceImpl.createJwtAuthTokenByUserId(userRepository.findUserByPhone(userPhone).getId());
		String refreshToken = this.refreshTokenServiceImpl.getRefreshToken(userRepository.findUserByPhone(userPhone)).getToken();

		log.info("User with id" + userRepository.findUserByPhone(userPhone).getId() + "receive tokens: \n" + "refreshToken: " + refreshToken + "\n accessToken: " + jwtAuthToken);

		return new ResponseAuthComplete(jwtAuthToken, refreshToken);
	}
}
