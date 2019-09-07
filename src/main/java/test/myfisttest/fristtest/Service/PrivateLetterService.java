package test.myfisttest.fristtest.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.myfisttest.fristtest.DTO.PrivateLetterDTO;
import test.myfisttest.fristtest.Entity.Privateletter;
import test.myfisttest.fristtest.Entity.PrivateletterExample;
import test.myfisttest.fristtest.Entity.User;
import test.myfisttest.fristtest.Mapper.PrivateletterExtMapper;
import test.myfisttest.fristtest.Mapper.PrivateletterMapper;
import test.myfisttest.fristtest.Mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PrivateLetterService {

    @Autowired
    PrivateletterMapper privateletterMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PrivateletterExtMapper privateletterExtMapper;

    public List<PrivateLetterDTO>  list(Long receiveId) {
        List<PrivateLetterDTO> privateLetterDTOS=new ArrayList<>();

        PrivateletterExample example = new PrivateletterExample();
        example.createCriteria().andReceiverIdEqualTo(receiveId);
        List<Privateletter> privateletters = privateletterMapper.selectByExample(example);
        PrivateLetterDTO privateLetterDTO = new PrivateLetterDTO();
        for (Privateletter privateletter : privateletters) {
            Long sendId = privateletter.getSendId();
            privateLetterDTO.setSendId(sendId);
            User user = userMapper.selectByPrimaryKey(sendId);
            privateLetterDTO.setPersonSendAratorUrl(user.getAvatarUrl());
            privateLetterDTO.setSendName(user.getName());
            privateLetterDTOS.add(privateLetterDTO);
        }
//去重
        privateLetterDTOS.stream()
                .collect(Collectors.toMap(PrivateLetterDTO::getSendId, Function.identity(), (oldValue, newValue) -> oldValue))
                .values()
                .stream();

        return privateLetterDTOS;
    }

//
    public List<PrivateLetterDTO> selectpersonletter(long sendid, Long id) {
//        s-id s-name r-id content
        List<PrivateLetterDTO> privateLetterDTOS=new ArrayList<>();
        List<Privateletter> privateletterList = privateletterExtMapper.selectContent(sendid,id);
        PrivateLetterDTO letterDTO = new PrivateLetterDTO();
        for (Privateletter privateletter : privateletterList) {
            letterDTO.setSendId(privateletter.getSendId());
            User user = userMapper.selectByPrimaryKey(privateletter.getSendId());
            letterDTO.setSendName(user.getName());
            letterDTO.setReceiveId(id);
            letterDTO.setContent(privateletter.getContent());
            privateLetterDTOS.add(letterDTO);
        }
        return privateLetterDTOS;
    }
}
