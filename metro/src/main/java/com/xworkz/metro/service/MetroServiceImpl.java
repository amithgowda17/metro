package com.xworkz.metro.service;

import com.xworkz.metro.dto.LoginDto;
import com.xworkz.metro.dto.RegisterDto;
import com.xworkz.metro.entity.LoginEntity;
import com.xworkz.metro.entity.RegisterEntity;
import com.xworkz.metro.repositry.MetroRepo;
import com.xworkz.metro.util.EncryptionDecryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;

import java.time.LocalTime;

@Slf4j
@Service
public class MetroServiceImpl implements MetroService {

    @Autowired
    EmailSent emailSent;

    @Autowired
    MetroRepo metroRepo;

    @Autowired
    EncryptionDecryption encryptionDecryption;

    @Override
    public String registerInService(RegisterDto registerDto) {
        System.out.println("====dto===" + registerDto);

        RegisterEntity registerEntity1 = metroRepo.findByEmail(registerDto.getEmail());

        RegisterEntity registerEntity2 = metroRepo.findByPhNo(registerDto.getPhNo());

        if (registerEntity1 != null || registerEntity2 != null) {

            return null;

        } else if (registerDto.getPassword().equals(registerDto.getConfirmpassword())) {

            registerDto.setPassword(encryptionDecryption.encrypt(registerDto.getPassword()));

            RegisterEntity registerEntity = new RegisterEntity();

            BeanUtils.copyProperties(registerDto, registerEntity);
            System.out.println("=====" + registerEntity);
            boolean isSaved = metroRepo.register(registerEntity);
            if (isSaved == true) {
                return "Registration Successfull";
            }
            return null;

        } else {
            return null;
        }
    }

    @Override
    public RegisterDto findByEmailInService(String email) {
        RegisterEntity registerEntity = metroRepo.findByEmail(email);

        if (registerEntity != null) {
            RegisterDto registerDto = new RegisterDto();
            BeanUtils.copyProperties(registerEntity, registerDto);
            return registerDto;
        }
        return null;
    }

    @Override
    public boolean findByPhInService(String phNo) {
        RegisterEntity registerEntity = metroRepo.findByPhNo(phNo);
        if (registerEntity != null) {
            RegisterDto registerDto = new RegisterDto();
            BeanUtils.copyProperties(registerEntity, registerDto);
            return true;
        }
        return false;
    }


    @Override
    public String loginDetails(LoginDto loginDto) {

        RegisterDto registerDto=findByEmailInService(loginDto.getEmail());

        RegisterEntity registerEntity=new RegisterEntity();

        if (registerDto != null) {

            System.out.println("==encrypted password from repo==" + registerDto.getPassword());

            String decrypt = encryptionDecryption.decrypt(registerDto.getPassword());

            registerDto.setPassword(decrypt);

           log.info("decrypt in service login======" +decrypt);

            if ((registerDto.getEmail()).equals(loginDto.getEmail()) && (registerDto.getPassword()).equals(loginDto.getPassword())) {

                loginDto.setLoginDate(LocalDate.now().toString());
                loginDto.setLoginTime(LocalTime.now().toString());
                loginDto.setLogoutTime(null);

                loginDto.setPassword(encryptionDecryption.encrypt(loginDto.getPassword()));

                LoginEntity loginEntity = new LoginEntity();

                BeanUtils.copyProperties(loginDto, loginEntity);

                metroRepo.login(loginEntity);

                registerDto.setNoOfAttempts(0);
                registerDto.setAccountLocked(false);

                BeanUtils.copyProperties(registerDto,registerEntity);

                metroRepo.userLocked(loginEntity.getEmail(),registerEntity.getNoOfAttempts(),registerEntity.isAccountLocked());

                return null;

            } else {
                registerDto.setNoOfAttempts(registerDto.getNoOfAttempts()+1);

                BeanUtils.copyProperties(registerDto,registerEntity);

                metroRepo.userLocked(loginDto.getEmail(),registerEntity.getNoOfAttempts(),registerEntity.isAccountLocked());

                if (registerDto.getNoOfAttempts()>=3){

                    registerDto.setAccountLocked(true);
                    BeanUtils.copyProperties(registerDto,registerEntity);

                    metroRepo.userLocked(loginDto.getEmail(),registerEntity.getNoOfAttempts(),registerEntity.isAccountLocked());
                    return null;
                }
                return "invalid password";
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean generateOtpInService(String email,String otp) {
        RegisterEntity registerEntity=metroRepo.findByEmail(email);
        if(registerEntity!=null){
            String otpSave=emailSent.emailSend(email);
           String encryptOtp = encryptionDecryption.encrypt(otpSave);
            metroRepo.saveOtpInRepo(email,encryptOtp);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyOtp(String email, String otpEntered) {
       RegisterEntity registerEntity = metroRepo.findByEmail(email);
        if (registerEntity!=null ) {
            String decryptOtp = encryptionDecryption.decrypt(registerEntity.getOtp());
            registerEntity.setOtp(decryptOtp);
            RegisterDto registerDto = new RegisterDto();
            BeanUtils.copyProperties(registerEntity, registerDto);
            if (otpEntered.equals(registerDto.getOtp())) {
                return true;
            }
            return false;
        }
        return false;
        }

    @Override
    public boolean updatePasswordInService(String email, String password, String confirmpassword) {
        RegisterDto registerDto=findByEmailInService(email);
        if(registerDto!=null) {
            if (password.equals(confirmpassword)){

                registerDto.setNoOfAttempts(0);
                registerDto.setAccountLocked(false);
                RegisterEntity registerEntity=new RegisterEntity();
                BeanUtils.copyProperties(registerDto,registerEntity);
                metroRepo.userLocked(email,registerEntity.getNoOfAttempts(),registerEntity.isAccountLocked());
                String encryptPassword=encryptionDecryption.encrypt(password);
                metroRepo.updatePasswordInRepo(email,encryptPassword);
                return true;
            }
           return false;
        }
        return false;
    }


}