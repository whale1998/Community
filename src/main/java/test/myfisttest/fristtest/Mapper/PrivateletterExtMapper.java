package test.myfisttest.fristtest.Mapper;

import org.apache.ibatis.annotations.Param;
import test.myfisttest.fristtest.Entity.Privateletter;

import java.util.List;

public interface PrivateletterExtMapper {

    List<Privateletter> selectContent(@Param("sendid") long sendid, @Param("receiveid") Long receiveid);
}