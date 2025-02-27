package hikerian.timetabling.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import hikerian.timetabling.dto.GradeClass;


@Mapper
public interface GradeClassMapper {
	int insertGradeClass(GradeClass gradeClass);
	List<GradeClass> getAllGradeClass();

}
