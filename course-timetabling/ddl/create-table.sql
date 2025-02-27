drop table tt_grade_class;
create table tt_grade_class(
	grade integer not null,
	class_nm varchar(10) not null,
	primary key (grade, class_nm)
);

insert into tt_grade_class(grade, class_nm) values(1, 'A');
insert into tt_grade_class(grade, class_nm) values(1, 'B');
insert into tt_grade_class(grade, class_nm) values(1, 'C');

insert into tt_grade_class(grade, class_nm) values(2, 'A');
insert into tt_grade_class(grade, class_nm) values(2, 'B');
insert into tt_grade_class(grade, class_nm) values(2, 'C');

insert into tt_grade_class(grade, class_nm) values(3, 'A');
insert into tt_grade_class(grade, class_nm) values(3, 'B');
insert into tt_grade_class(grade, class_nm) values(3, 'C');