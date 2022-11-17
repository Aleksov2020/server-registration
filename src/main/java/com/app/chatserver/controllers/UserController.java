package com.app.chatserver.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import com.app.chatserver.dto.Response;
import com.app.chatserver.dto.ResponseAuthComplete;
import com.app.chatserver.dto.ResponseTokenProvider;
import com.app.chatserver.dto.ResponseUserList;
import com.app.chatserver.exceptions.TokenException;
import com.app.chatserver.model.RefreshToken;
import com.app.chatserver.services.JwtTokenServiceImpl;
import com.app.chatserver.services.RefreshTokenServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.app.chatserver.services.UserService;

@RestController
@Log4j2
public class UserController {
	private UserService userService;
	private RefreshTokenServiceImpl refreshTokenServiceImpl;
	private JwtTokenServiceImpl jwtTokenServiceImpl;

	@Autowired
    public UserController(UserService userService, RefreshTokenServiceImpl refreshTokenServiceImpl, JwtTokenServiceImpl jwtTokenServiceImpl) {
        this.userService = userService;
		this.refreshTokenServiceImpl = refreshTokenServiceImpl;
		this.jwtTokenServiceImpl = jwtTokenServiceImpl;
    }
	@CrossOrigin(origins = "http://localhost:8090")
	@PostMapping("/reg/v1/registration")
	public ResponseEntity<ResponseTokenProvider> sendSmsCodeTo(@RequestParam String userPhone,
															   @RequestParam String userName )
														 throws InvalidKeyException, NoSuchAlgorithmException {
		return new ResponseEntity <> (
				this.userService.createTokenAndSendSmsCodeToUser(userPhone, userName),
				HttpStatus.OK
		);
	}
	@PostMapping("/reg/v1/validateCodeAndRegisterUser/{tokenProvider}")
	public ResponseEntity<ResponseAuthComplete> validateSmsCodeAndRegister( @PathVariable String tokenProvider,
																 @RequestParam String code )
			throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		return new ResponseEntity <> (
				this.userService.validateSmsCodeAndRegisterUser(tokenProvider, code),
				HttpStatus.OK
		);
	}

	@PostMapping("/reg/v1/validateCodeAndLoginUser/{tokenProvider}")
	public ResponseEntity<ResponseAuthComplete> validateSmsCodeAndLogin ( @PathVariable String tokenProvider,
																		  @RequestParam String code )
															throws NoSuchAlgorithmException, InvalidKeyException {
		return new ResponseEntity <> (
				this.userService.validateSmsCodeAndLoginUser(tokenProvider, code),
				HttpStatus.OK
		);
	}
	/*Not working
	@PostMapping("/checkSmsCode/{token}/attempt")
	public ResponseEntity<Response> checkSmsCodeAttempt( @PathVariable(required = true) String token,
														 @PathVariable(required = true) String num,
														 @RequestParam(required = true) String code )
														   throws NoSuchAlgorithmException,
														   		InvalidKeyException {
		solve with database.
	}
	*/
	@PostMapping("/reg/v1/login")
	public ResponseEntity<ResponseTokenProvider> login( @RequestParam String userPhone )
														   throws NoSuchAlgorithmException, InvalidKeyException {
		return new ResponseEntity <> (
				this.userService.createTokenAndSendAuthSmsCodeToUser(userPhone),
				HttpStatus.OK
		);
	}
	@PostMapping("/reg/v1/refreshToken")
	public ResponseEntity<ResponseAuthComplete> refreshToken(@RequestParam String refreshToken) {
		return refreshTokenServiceImpl.findByToken(refreshToken)
				.map(refreshTokenServiceImpl::verifyExpiration)
				.map(RefreshToken::getUser)
				.map(user -> {
					String token = this.jwtTokenServiceImpl.createJwtAuthTokenByUserId(user.getId());
					return ResponseEntity.ok(new ResponseAuthComplete(token, refreshToken));
				})
				.orElseThrow(() -> new TokenException("Refresh token is not in database!"));
	}

	@PostMapping("/user/v1/changeUserName/{accessToken}")
	public ResponseEntity<Response> changeUserName(	@PathVariable String accessToken,
													@RequestParam String newUserName ) {
		jwtTokenServiceImpl.validateToken(accessToken);
		userService.changeUserName(
				newUserName,
				(Integer) jwtTokenServiceImpl.getAllClaimsFromToken(accessToken).get("id")
		);
		return new ResponseEntity<>(
				new Response("CHANGE_SUCCESSFUL"),
				HttpStatus.OK
		);
	}

	@PostMapping("/user/v1/changeUserAvatar/{accessToken}")
	public ResponseEntity<Response> changeUserAvatar(	@PathVariable String accessToken,
													@RequestParam String newImageBase64 ) throws IOException {
		jwtTokenServiceImpl.validateToken(accessToken);
		userService.changeUserAvatar(
				newImageBase64,
				(Integer) jwtTokenServiceImpl.getAllClaimsFromToken(accessToken).get("id")
		);
		return new ResponseEntity<>(
				new Response("CHANGE_SUCCESSFUL"),
				HttpStatus.OK
		);
	}

	@GetMapping("/user/v1/search/{accessToken}")
	public ResponseEntity<ResponseUserList> changeUserName(@PathVariable String accessToken,
														   @RequestParam String userName,
														   @RequestParam(defaultValue = "0", required = false) Integer page,
														   @RequestParam(defaultValue = "10", required = false) Integer pageSize) throws IOException {
		jwtTokenServiceImpl.validateToken(accessToken);
		return new ResponseEntity<>(
				userService.searchUsers(userName,page,pageSize),
				HttpStatus.OK
		);
	}

	@PostMapping("/reg/v1/additionalInfo/{token}")
	public ResponseEntity<Response> additionalInfo( @PathVariable String token,
													@RequestParam(required = false) String lastName,
													@RequestParam(required = false) String name,
													@RequestParam(required = false) String birthDate, 
													@RequestParam(required = false) MultipartFile image)
			throws ParseException, IOException {
		return new ResponseEntity <> (
				new Response(
						this.userService.updateAdditionalInfo(
							lastName,
							name,
							birthDate,
							token,
							image)
				),
				HttpStatus.OK
		);
	}
}
