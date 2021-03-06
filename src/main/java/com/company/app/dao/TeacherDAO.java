package com.company.app.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.CalendarType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import com.company.app.dto.SchoolGroupDTO;
import com.company.app.dto.StudentDTO;
import com.company.app.dto.TeacherDTO;
import com.company.app.model.Teacher;


@Stateless
public class TeacherDAO extends DAO<Teacher> {
	
	  @SuppressWarnings("unused")
	  private static final Logger LOGGER = Logger.getLogger(TeacherDAO.class); 
	  
	  @PersistenceContext 
	  private EntityManager entityManager;
	  
	  @PostConstruct
	  public void init() {
	    super.init(entityManager, Teacher.class);
	  }
	  
  	public TeacherDTO consulta(Teacher professorED){
  		//consulta professor
  		StringBuffer sqlProfessor = new StringBuffer();
  		sqlProfessor.append("Select id_professor as idProfessor, \n");
  		sqlProfessor.append("	nome as nome, \n");
  		sqlProfessor.append("	id_funcional as idFuncional \n");
  		sqlProfessor.append("From professor  \n");
  		sqlProfessor.append("Where id_teacher = :idTeacher \n");
  	    Session session = entityManager.unwrap(Session.class);
  	    SQLQuery query = session.createSQLQuery(sqlProfessor.toString());
  	    query.setParameter("idTeacher", professorED.getIdTeacher());
  	    query.addScalar("idProfessor", LongType.INSTANCE);
  	    query.addScalar("nome", StringType.INSTANCE);
  	    query.addScalar("idFuncional", StringType.INSTANCE);
  	    query.setResultTransformer(new AliasToBeanResultTransformer(TeacherDTO.class));
  	    TeacherDTO professorDTO = (TeacherDTO) query.uniqueResult();
  	    
  	    //consulta turma
  		StringBuffer sqlTurma = new StringBuffer();
  		sqlTurma.append("Select t.id_turma as idTurma, \n");
  		sqlTurma.append("       t.nome as nome, \n");
  		sqlTurma.append("       t.id_escola_sis_origem as idEscola, \n");
  		sqlTurma.append("       t.nome_escola as nomeEscola, \n");
  		sqlTurma.append("       t.nome_calendario as nomeCalendario, \n");
  		sqlTurma.append("       t.dt_inicio_calendario as dtInicioCalendario, \n");
  		sqlTurma.append("       t.dt_fim_calendario as dtFimCalendario, \n");
  		sqlTurma.append("       t.nome_serie as nomeSerie, \n");
  		sqlTurma.append("       t.id_professor as idProfessor, \n");
  		sqlTurma.append("       t.nome_curso as nomeCurso \n");
  		sqlTurma.append("From turma t, \n");
  		sqlTurma.append("     professor p  \n");
  		sqlTurma.append("Where p.id_professor = t.id_professor \n");
  		sqlTurma.append("And  p.id_funcional = :idFuncional \n");
  		SQLQuery queryTurma = session.createSQLQuery(sqlTurma.toString());
  		queryTurma.addScalar("idTurma", LongType.INSTANCE);
  		queryTurma.addScalar("nome", StringType.INSTANCE);
  		queryTurma.addScalar("idEscola", LongType.INSTANCE);
  		queryTurma.addScalar("nomeEscola", StringType.INSTANCE);
  		queryTurma.addScalar("nomeCalendario", StringType.INSTANCE);
  		queryTurma.addScalar("dtInicioCalendario", CalendarType.INSTANCE);
  		queryTurma.addScalar("dtFimCalendario", CalendarType.INSTANCE);
  		queryTurma.addScalar("nomeSerie", StringType.INSTANCE);
  		queryTurma.addScalar("idProfessor", LongType.INSTANCE);
  		queryTurma.addScalar("nomeCurso", StringType.INSTANCE);
  		queryTurma.setResultTransformer(new AliasToBeanResultTransformer(SchoolGroupDTO.class));
  		List<SchoolGroupDTO> listaTurma = queryTurma.list();
  		
  		//consulta alunos
  		StringBuffer sqlAluno = new StringBuffer();
  		sqlAluno.append("Select a.id_aluno as idAluno, \n");
  		sqlAluno.append("	m.id_aluno_turma as idAlunoTurma, \n");
  		sqlAluno.append("	a.nome as nome \n");
  		sqlAluno.append("From aluno a, \n");
  		sqlAluno.append("     aluno_turma m \n");
  		sqlAluno.append("Where m.id_aluno = a.id_aluno  \n");
  		sqlAluno.append("And m.id_turma = :idTurma \n");
  		sqlAluno.append("Order By a.nome \n");
  		SQLQuery queryAluno = session.createSQLQuery(sqlAluno.toString());
  		queryAluno.setResultTransformer(new AliasToBeanResultTransformer(StudentDTO.class));
  		if(listaTurma!=null && !listaTurma.isEmpty()){
  			for(SchoolGroupDTO turma : listaTurma){
  				queryAluno.setParameter("idTurma", turma.getIdTurma());
  				queryAluno.addScalar("idAluno",LongType.INSTANCE);
  				queryAluno.addScalar("idAlunoTurma",LongType.INSTANCE);
  				queryAluno.addScalar("nome",StringType.INSTANCE);
  				List<StudentDTO> listaAlunos = queryAluno.list();
  				turma.setAlunos(listaAlunos);
  			}
  		}
  		professorDTO.setTurmas(listaTurma);
  	    return professorDTO;
  	}

}
