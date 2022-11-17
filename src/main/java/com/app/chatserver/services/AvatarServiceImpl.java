package com.app.chatserver.services;

import com.app.chatserver.model.Avatar;
import com.app.chatserver.repository.AvatarRepository;
import org.springframework.stereotype.Service;

@Service
public class AvatarServiceImpl implements AvatarService{
    private AvatarRepository avatarRepository;

    public AvatarServiceImpl(AvatarRepository avatarRepository){
        this.avatarRepository = avatarRepository;
    }
    @Override
    public void saveAvatar(Avatar avatar) {
        this.avatarRepository.save(avatar);
    }
}
