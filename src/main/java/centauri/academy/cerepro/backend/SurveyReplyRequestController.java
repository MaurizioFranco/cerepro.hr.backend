/**
 * 
 */
package centauri.academy.cerepro.backend;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import centauri.academy.cerepro.persistence.entity.CeReProAbstractEntity;
import centauri.academy.cerepro.persistence.entity.SurveyReply;
import centauri.academy.cerepro.persistence.entity.UserTokenSurvey;
import centauri.academy.cerepro.persistence.entity.custom.CustomErrorType;
import centauri.academy.cerepro.persistence.repository.SurveyInterviewRepository;
import centauri.academy.cerepro.persistence.repository.SurveyRepository;
import centauri.academy.cerepro.persistence.repository.UserRepository;
import centauri.academy.cerepro.persistence.repository.surveyreply.SurveyReplyRepository;
import centauri.academy.cerepro.persistence.repository.usersurveytoken.UserSurveyTokenRepository;
import centauri.academy.cerepro.rest.request.SurveyReplyRequest;
import centauri.academy.cerepro.rest.request.SurveyReplyStartRequest;
import centauri.academy.cerepro.service.SurveyReplyRequestService;

/**
 * @author Marco Fulgosi
 *
 */

@RestController
@RequestMapping("/api/v1/surveyreplyrequest")
public class SurveyReplyRequestController {

	@Autowired
	private SurveyRepository surveyRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SurveyReplyRepository surveyReplyRepository;
	@Autowired
	private SurveyReplyRequestService surveyReplyRequestService;
	@Autowired
	private UserSurveyTokenRepository userSurveyTokenRepository;
	@Autowired
	private SurveyInterviewRepository surveyInterviewRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(SurveyReplyRequestController.class);
	/** CREATE A NEW SURVEY REPLY REQUEST */
	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CeReProAbstractEntity> insertSurveyReply(@Valid @RequestBody final SurveyReplyRequest surveyReplyRequest) {
		logger.info("Creating Survey Reply : {}", surveyReplyRequest);
		
		if (surveyRepository.findById(surveyReplyRequest.getSurveyId())==null)
			return new ResponseEntity<>(
					new CustomErrorType("Unable to create new Survey Reply. Survey Id: " + surveyReplyRequest.getSurveyId()
							+ " is not present in database."),HttpStatus.CONFLICT);
		
		if (userRepository.findById(surveyReplyRequest.getUserId()) == null)
			return new ResponseEntity<>(
					new CustomErrorType("Unable to create new Survey Reply. User id: " + surveyReplyRequest.getUserId()
							+ " is not present in database."),HttpStatus.CONFLICT);
		
		/* Creating a SurveyReply by SurveyReplyRequest */
		SurveyReply surveyReply = new SurveyReply();
		surveyReply.setSurveyId(surveyReplyRequest.getSurveyId());
		surveyReply.setUserId(surveyReplyRequest.getUserId());
		surveyReply.setStarttime(surveyReplyRequest.getStarttime());
		surveyReply.setEndtime(surveyReplyRequest.getEndtime());
		
		surveyReply.setAnswers(surveyReplyRequestService.answersToString(surveyReplyRequest.getAnswers()));
		surveyReply.setPoints(surveyReplyRequestService.pointsCalculator(surveyReplyRequest.getAnswers()).toString()); 
		surveyReply.setPdffilename("Risposte del candidato n."+surveyReplyRequest.getUserId()+" Al questionario n."+surveyReplyRequest.getSurveyId());
		
		surveyReplyRepository.save(surveyReply);
		return new ResponseEntity<>(surveyReply, HttpStatus.CREATED);
	}
	
	/** CREATE A NEW START SURVEY REPLY */
	@PostMapping(value = "/start/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CeReProAbstractEntity> insertStartSurveyReply(@Valid @RequestBody final SurveyReplyStartRequest surveyReplyRequest) {
		logger.info("Creating Survey Reply : {}", surveyReplyRequest);
		
		if (surveyRepository.findById(surveyReplyRequest.getSurveyId())==null)
			return new ResponseEntity<>(
					new CustomErrorType("Unable to create new Survey Reply. Survey Id: " + surveyReplyRequest.getSurveyId()
							+ " is not present in database."),HttpStatus.CONFLICT);
		
		if (userRepository.findById(surveyReplyRequest.getUserId()) == null)
			return new ResponseEntity<>(
					new CustomErrorType("Unable to create new Survey Reply. User id: " + surveyReplyRequest.getUserId()
							+ " is not present in database."),HttpStatus.CONFLICT);
		
		/* Creating a SurveyReply by SurveyReplyRequest */
		SurveyReply surveyReply = new SurveyReply();
		surveyReply.setSurveyId(surveyReplyRequest.getSurveyId());
		surveyReply.setUserId(surveyReplyRequest.getUserId());
		surveyReply.setStarttime(LocalDateTime.now());
		
			
		surveyReplyRepository.save(surveyReply);
		
		System.out.println("surveyReplyRequest.getUserTokenId() "+surveyReplyRequest.getUserTokenId());
		
		Optional<UserTokenSurvey> userTokenSurvey = userSurveyTokenRepository.findById(surveyReplyRequest.getUserTokenId());

		UserTokenSurvey currentUserTokenSurvey = userTokenSurvey.get();
		currentUserTokenSurvey.setExpired(true);
		userSurveyTokenRepository.save(currentUserTokenSurvey); 

		return new ResponseEntity<>(surveyReply, HttpStatus.CREATED);
	}
	
	/** END SURVEY REPLY */
	@PutMapping(value = "/end/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CeReProAbstractEntity> updateSurvay(@PathVariable("id") final Long id, @RequestBody SurveyReplyRequest surveyReplyRequest) {


		Optional<SurveyReply> optSurveyReply = surveyReplyRepository.findById(id);

		if (!optSurveyReply.isPresent()) {
			return new ResponseEntity<>(new CustomErrorType("Unable to upate. User with id " + id + " not found."), HttpStatus.NOT_FOUND);
		}
		
		SurveyReply currentSurveyReply = optSurveyReply.get(); 
		currentSurveyReply.setEndtime(LocalDateTime.now());
		if(surveyInterviewRepository.findBySurveyId(currentSurveyReply.getSurveyId()).size() == 0) {
			System.out.println("----------------------------------------------AAA--------------------");
			currentSurveyReply.setAnswers(surveyReplyRequestService.answersToString(surveyReplyRequest.getAnswers()));
			currentSurveyReply.setPoints(surveyReplyRequestService.pointsCalculator(surveyReplyRequest.getAnswers()).toString()); 
		}
		else {
			System.out.println("----------------------------------------------BBB--------------------");
			currentSurveyReply.setAnswers(surveyReplyRequestService.answersInterviewToString(surveyReplyRequest.getInterviewAnswers()));
			
		}
	 
		surveyReplyRepository.save(currentSurveyReply); 
		return new ResponseEntity<>(currentSurveyReply, HttpStatus.OK);
	}
	
}
