/**
 * 
 */
package centauri.academy.cerepro.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import centauri.academy.cerepro.persistence.entity.Candidate;
import centauri.academy.cerepro.persistence.entity.CandidateStates;
import centauri.academy.cerepro.persistence.entity.Role;
import centauri.academy.cerepro.persistence.entity.User;
import centauri.academy.cerepro.persistence.entity.custom.CandidateCustom;
import centauri.academy.cerepro.persistence.entity.custom.CustomErrorType;
import centauri.academy.cerepro.persistence.entity.custom.ListedCandidateCustom;
import centauri.academy.cerepro.persistence.repository.candidate.CandidateRepository;
import centauri.academy.cerepro.rest.request.RequestCandidateCustom;

/**
 * 
 * 
 * @author m.franco@proximainformatica.com
 *
 */
@Service 
public class CandidateService {
	
	@Value("${app.folder.candidate.profile.img}")
	public String IMG_DIR;
	@Value("${app.folder.candidate.cv}")
	public String CV_DIR;	
	
	public static final Logger logger = LoggerFactory.getLogger(CandidateService.class);
	
	@Autowired
	private CandidateRepository candidateRepository;
	@Autowired
	private CoursePageService coursePageService ;
	/**
	 * Gets all candidates entries from table
	 * 
	 * @return List<Candidate>
	 */
	public List<Candidate> getAll () {
		logger.info("CandidateService.getAll - START");
		return candidateRepository.findAll();
	}
	
	/**
	 * Try to delete all instances into candidate table
	 * 
	 * @return long
	 */
	public void deleteAll () {
		logger.info("deleteAll - START");
		candidateRepository.deleteAll();
	}
	
//	/**
//	 * Gets all candidates custom(Candidates + Users)
//	 * COMMENTED ON 24/01/20 because no more used!!!!!!!! by maurizio
//	 * @return List<CandidateCustom>
//	 */
//	public List<CandidateCustom> getAllCustom () {
//		log.info("CandidateService.getAllCustom - START");
//		return candidateRepository.getAllCustomCandidates();
//	}
	
//	/**
//	 * Gets all candidates custom(Candidates + Users) in paginated version list
//	 * 
//	 * @param Pageable p, page info
//	 * @return Page<CandidateCustom>
//	 */
//	public Page<CandidateCustom> getAllCustomPaginated (Pageable p) {
//		log.info("CandidateService.getAllCustomPaginated - START - with pageable info {}", p);
//		return candidateRepository.getAllCustomCandidatesPaginated(p);
//	}
	
	/**
	 * Gets all candidates custom(Candidates + Users) in paginated version list, filtering by course code field
	 * 
	 * @param Pageable p, page info
	 * @param String courseCode, string value for course code field
	 * @return Page<ListedCandidateCustom>
	 */
	public Page<ListedCandidateCustom> getAllCustomPaginatedByCourseCode (Pageable p, String courseCode) {
		logger.info("CandidateService.getAllCustomPaginatedByCourseCode - START - with pageable info {0} and course code: {1}", p, courseCode);
		return candidateRepository.getAllCustomCandidatesPaginatedByCourseCode(p, courseCode);
	}
	
	/**
	 * Gets all candidates custom(Candidates + Users) by course code field
	 * 
	 * @return List<CandidateCustom>
	 */
	public List<ListedCandidateCustom> getAllByCourseCode (String courseCode) {
		logger.info("CandidateService.getAllByCourseCode - START with given course code {}", courseCode);
		List<ListedCandidateCustom> items = candidateRepository.findByCourseCode(courseCode);
		return items;
	}
	
	/**
	 * Insert new candidate entity 
	 */
	public Candidate insert (Candidate c) {
		logger.info("insert() - START - with given candidate {}", c);
//		c.setCandidacyDateTime(LocalDateTime.now());
//		c.setCandidateStatesId(CandidateStates.DEFAULT_INSERTING_STATUS_CODE);
//		logger.info("CandidateService.insert DEBUG with given candidate {}", c);
		return candidateRepository.save(c);
	}
	
	/**
	 * Update candidate entity 
	 * 
	 */
	public Candidate update (Candidate c) {
		logger.info("update() - START -with given candidate {}", c);
		return candidateRepository.save(c);
	}
	
	/**
	 * Retrieve candidate by id
	 * 
	 * @param id of the candidate to retrieve
	 * @return candidate entity
	 */
	public Optional<Candidate> getById(long id) {
		logger.info("getById() - START with given id {}", id);
		return candidateRepository.findById(id);
		
	}
	
	/**
	 * Retrieve candidate by id
	 * 
	 * @param id of the candidate to retrieve
	 * @return candidate entity
	 */
	public CandidateCustom getCustomById(long id) {
		logger.info("getCustomById() - START - with given id {}", id);
		CandidateCustom candiateToReturn = null ;
        try {
        	candiateToReturn = candidateRepository.getSingleCustomCandidate(id);
        } catch (Exception e) {
        	logger.info(e.getMessage());
			logger.info("getCustomById() - INFO - No candidate found for id {}", id);
        }
		return candiateToReturn ;
		
	}
	
	/**
	 * Delete candidate by id
	 * 
	 * @param id of the candidate to delete
	 */
	public void deleteById(long id) {
		logger.info("CandidateService.deleteById START with given id {}", id);
		candidateRepository.deleteById(id);
	}
	
//	/**
//	 * Insert new candidate custom by inserting into candidates and users tables.
//	 * 
//	 * @param CandidateCustom cc, candidate custom info to insert
//	 */
//	public void insertCustom (CandidateCustom cc) {
//		log.info("CandidateService.insert START with given candidate {}", c);
//		c.setCandidacyDateTime(LocalDateTime.now());
//		c.setCandidateStatesId(CandidateStates.DEFAULT_INSERTING_STATUS_CODE);
//		log.info("CandidateService.insert DEBUG with given candidate {}", c);
//		candidateRepository.save(c);
//	}
	
	public long getRegisteredCandidatesInDate(LocalDate date) {
		logger.info("getRegisteredCandidatesInDate - START");
		LocalDateTime start = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59);
		long count = candidateRepository.getCandidateCountWithRegdateInPeriod(start, end);
	
		logger.info("getRegisteredCandidatesInDate - END - with registered candidates: " + count);
		return count;
	}
	
	/**
	 * 
	 * Returns number of candidates registered from days ago number received.
	 * 
	 * @param daysAgo
	 * @return
	 * @author orlando
	 * @author m.franco@proximanetwork.it
	 */
	public long getRegisteredCandidatesFromDaysAgo(long daysAgo) {
		logger.info("getRegisteredCandidatesFromDaysAgo - START - daysAgo: " + daysAgo);
		long count = 0;
		LocalDate date = LocalDate.now();
		//User currentUser;
		for(int i = 0; i < daysAgo; i++) {
			LocalDate day = date.minusDays(i);
			count += getRegisteredCandidatesInDate(day);		
		}
		logger.info("getRegisteredCandidatesFromDaysAgo - END - return: " + count);
		return count;
	}
	
	/**
	 * create new candidate entity
	 * 
	 * @return Candidate, inserted entity
	 */
	public Candidate createNewCandidate (RequestCandidateCustom requestCandidateCustom) {
		logger.info("createNewCandidate - START - with requestCandidateCustom {}", requestCandidateCustom);
		
//		if (roleRepository.findByLevel(Role.JAVA_COURSE_CANDIDATE_LEVEL) == null) {
//
//			return new ResponseEntity<>(
//					new CustomErrorType("Unable to create new Candidate. Level " + Role.JAVA_COURSE_CANDIDATE_LEVEL + " is not present in database."),
//					HttpStatus.CONFLICT);
//		}
//		User user = null ;
//		Optional<User> optUser = userRepository.findByEmail(candidateCustom.getEmail()) ;
//		if (optUser.isPresent()) {
//			user = optUser.get();
//			return new ResponseEntity<>(new CustomErrorType("Unable to create new user. A Candidate with email "
//					+ requestCandidateCustom.getEmail() + " already exist."), HttpStatus.CONFLICT);
//		} else { 
//
//
//			user = new User();
//	
//			user.setEmail(requestCandidateCustom.getEmail());
//			user.setFirstname(requestCandidateCustom.getFirstname());
//			user.setLastname(requestCandidateCustom.getLastname());
//			user.setNote(requestCandidateCustom.getNote());
//			System.out.println("Local date time: " + requestCandidateCustom.getDateOfBirth());
//			Date inputDate = requestCandidateCustom.getDateOfBirth();
//	
//			if (inputDate != null) {
//				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
//				String inputStringDate = formatter.format(inputDate);
//				System.out.println("inputStringDate " + inputStringDate);
//	
//				if (inputStringDate.equals("11-nov-1111")) {
//					user.setDateOfBirth(null);
//				} else {
//					LocalDate dateToDB = inputDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//					user.setDateOfBirth(dateToDB);
//				}
//			}
//			user.setRegdate(LocalDateTime.now());
//			user.setRole(Role.JAVA_COURSE_CANDIDATE_LEVEL);
//	
//			//User userforCandidate = userRepository.save(user);
//			userRepository.save(user);
//		}
		Candidate candidateToInsert = new Candidate () ;
		

		
		
		

		candidateToInsert.setUserId(requestCandidateCustom.getInsertedBy());
		candidateToInsert.setInsertedBy(requestCandidateCustom.getInsertedBy());
		candidateToInsert.setDomicileCity(requestCandidateCustom.getDomicileCity());
//		candidateToInsert.setDomicileHouseNumber(requestCandidateCustom.getDomicileHouseNumber());
//		candidateToInsert.setDomicileStreetName(requestCandidateCustom.getDomicileStreetName());
		candidateToInsert.setStudyQualification(requestCandidateCustom.getStudyQualification());
		candidateToInsert.setGraduate(requestCandidateCustom.getGraduate());
		candidateToInsert.setHighGraduate(requestCandidateCustom.getHighGraduate());
		candidateToInsert.setStillHighStudy(requestCandidateCustom.getStillHighStudy());
		candidateToInsert.setMobile(requestCandidateCustom.getMobile());
		candidateToInsert.setEmail(requestCandidateCustom.getEmail());
		candidateToInsert.setFirstname(requestCandidateCustom.getFirstname());
		candidateToInsert.setLastname(requestCandidateCustom.getLastname());
		candidateToInsert.setTechnicalNote(requestCandidateCustom.getNote());
		candidateToInsert.setRegdate(LocalDateTime.now());
		candidateToInsert.setCourseCode(coursePageService.checkCoursePageCode(requestCandidateCustom.getCourseCode()));
		//TODO: AGGIUNGERE UN CONTROLLO SUL COURSE CODE SE NON è PRESENTE NEL DATABASE METTERNE UNO DI DEFAULT --> candidatura generica!!!!
//		candidateToInsert.setNote(requestCandidateCustom.getNote());
		candidateToInsert.setCandidacyDateTime(LocalDateTime.now());
		candidateToInsert.setCandidateStatusCode(CandidateStates.DEFAULT_INSERTING_STATUS_CODE);
		if (requestCandidateCustom.getCvExternalPath() != null) {

			try {

				String[] nameIdData = uploadFile(requestCandidateCustom.getFiles(), ""+requestCandidateCustom.getEmail().hashCode());
				logger.info("nameIdData:" + nameIdData[1]);
				candidateToInsert.setCvExternalPath(nameIdData[1]);
			} catch (IOException e) {
				logger.error("Error", e);
			}
		}
		if (requestCandidateCustom.getImgpath() != null) {

			try {
				String[] nameIdData = uploadFile(requestCandidateCustom.getFiles(), ""+requestCandidateCustom.getEmail().hashCode());
				logger.info("nameIdData:" + nameIdData[0]);
				candidateToInsert.setImgpath(nameIdData[0]);
				//userforCandidate = userRepository.save(user);
//				userRepository.save(user);
			} catch (IOException e) {
				logger.error("Error", e);
			}

		}
		return insert(candidateToInsert);

	}
	
	/*
	 * Provide to save file
	 */
	public String[] uploadFile(MultipartFile[] files, String candidateFileName) throws IOException {
		logger.info("saveUploadedFiles - START");
		// Make sure directory exists!
		File uploadImgDir = new File(IMG_DIR);
		uploadImgDir.mkdirs();
		File uploadCvDir = new File(CV_DIR);
		uploadCvDir.mkdirs();

		StringBuilder sb = new StringBuilder();
		logger.info("saveUploadedFiles - DEBUG 1");
		String[] nameIdData = new String[2];
		for (MultipartFile file : files) {

			if (file.isEmpty()) {
				continue;
			}
			String uploadFilePath = null;
			logger.info("saveUploadedFiles - DEBUG 2");
			StringTokenizer st = new StringTokenizer(file.getOriginalFilename(), ".");
			String name = st.nextToken();
			String extension = st.nextToken();
			String fileName = file.getOriginalFilename();

			if (fileName.endsWith("jpg")) {
				extension = ".jpg";
				uploadFilePath = IMG_DIR + File.pathSeparator + candidateFileName + file.getOriginalFilename();
				nameIdData[0] = candidateFileName + extension;
				logger.info("saveUploadedFiles - DEBUG 2.1 - nameIdData[0]: " + nameIdData[0]);
				uploadFilePath = IMG_DIR + File.separatorChar + nameIdData[0];
			}
			if (fileName.endsWith("jpeg")) {
				extension = ".jpeg";
				uploadFilePath = IMG_DIR + File.pathSeparator + candidateFileName + file.getOriginalFilename();
				nameIdData[0] = candidateFileName + extension;
				logger.info("saveUploadedFiles - DEBUG 2.2 - nameIdData[0]: " + nameIdData[0]);
				uploadFilePath = IMG_DIR + File.separatorChar + nameIdData[0];
			}
			if (fileName.endsWith("png")) {
				extension = ".png";
				uploadFilePath = IMG_DIR + File.pathSeparator + candidateFileName + file.getOriginalFilename();
				nameIdData[0] = candidateFileName + extension;
				logger.info("saveUploadedFiles - DEBUG 2.3 - nameIdData[0]: " + nameIdData[0]);
				uploadFilePath = IMG_DIR + File.separatorChar + nameIdData[0];
			}
			if (fileName.endsWith("docx")) {
				extension = ".docx";
				nameIdData[1] = candidateFileName + extension;
				logger.info("saveUploadedFiles - DEBUG 2.4 - nameIdData[1]: " + nameIdData[1]);
				uploadFilePath = CV_DIR + File.separatorChar + nameIdData[1];
			}
			if (fileName.endsWith("doc")) {
				extension = ".doc";
				nameIdData[1] = candidateFileName + extension;
				logger.info("saveUploadedFiles - DEBUG 2.5 - nameIdData[1]: " + nameIdData[1]);
				uploadFilePath = CV_DIR + File.separatorChar + nameIdData[1];
			}
			if (fileName.endsWith("pdf")) {
				extension = ".pdf";
				nameIdData[1] = candidateFileName + extension;
				logger.info("saveUploadedFiles - DEBUG 2.6 - nameIdData[1]: " + nameIdData[1]);
				uploadFilePath = CV_DIR + File.separatorChar + nameIdData[1];
			}

			logger.info("saveUploadedFiles - DEBUG 3 - uploadFilePath: " + uploadFilePath);
			byte[] bytes = file.getBytes();
			logger.info("saveUploadedFiles - DEBUG 3.5 - bytes.length: " + bytes.length);
			FileOutputStream fos = new FileOutputStream(uploadFilePath);
			fos.write(bytes);

			logger.info("saveUploadedFiles - DEBUG 5");
		}
		logger.info("saveUploadedFiles - DEBUG 6");
		return nameIdData;
	}
}
