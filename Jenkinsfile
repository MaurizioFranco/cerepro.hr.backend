pipeline {
    agent any
    options { timeout(time: 5) }
    parameters {
        booleanParam(name: 'PROMOTE_ON_PRODUCTION', defaultValue: false,
            description: 'Al termine di questa pipeline, vuoi consentire la promozione in ambiente di Produzione?')
    }
    environment {
        PACKAGE_FILE_NAME = readMavenPom().getProperties().getProperty('package.file.name')
        MAVEN_FILE = readMavenPom()
        PACKAGING = readMavenPom().getPackaging()
        ARTIFACT_FULL_FILE_NAME = "${PACKAGE_FILE_NAME}.${PACKAGING}"
        /*        
        DEV_ENVIRONMENT_HOSTNAME = "eltanin"
        STAGE_ENVIRONMENT_HOSTNAME = "ndraconis"
        PROD_ENVIRONMENT_HOSTNAME = "thuban"
        ENVIRONMENT_TARGET_DIR = "cerepro_resources"
        ENVIRONMENT_DEPLOY_DIR = "tomcat_webapps"
        */
    }
    stages {        
        stage("Compile") {
            steps {
                sh "./mvnw clean compile"
            }
        }
        stage("Provides application property file for Integration tests --> for mvn:test goal purpose only!!!") {
            steps {  
                /*              
                sh "rm ./src/main/resources/mail.properties"
                sh "rm ./src/main/resources/application.properties"
                echo "Original ./src/main/resources/mail.properties and ./src/main/resources/application.properties successfully removed!!"
                sh "cp /cerepro_resources/properties/cerepro.mail.manager/mail.test.properties ./src/main/resources/mail.properties"
                //TO UNCOMMENT
                //sh "cp /cerepro_resources/properties/cerepro.hr.backend/application-test.properties ./src/test/resources/application.properties"
                */
                sh "cp /cerepro_resources/properties/cerepro.mail.manager/mail.test.properties      ./src/test/resources/mail.properties"
                sh "cp /cerepro_resources/properties/cerepro.hr.backend/application-test.properties ./src/test/resources/application.properties"
            }
        } 
        stage("Unit tests") {
            steps {
                sh "./mvnw test"
            }
        }
        stage("Publish code coverage(JaCoCo) code report") {
            steps {              
				jacoco(execPattern: 'target/jacoco.exec')
				publishHTML (target: [
				        reportDir: 'target/site/jacoco',
				        reportFiles: 'index.html',
				        reportName: "JaCoCo Report"
				    ]
				)
            }
        }
        stage("Publish checkstyle code report") {
            steps {
                sh "./mvnw site"
                publishHTML (target: [
					reportDir: 'target/site/',
					reportFiles: 'checkstyle.html',
					reportName: "Checkstyle Report"
				])
            }
        }
        stage ("PREPARE FULL PACKAGE") {
            steps {
                echo "Provides application.{env}.properties files"
                sh "cp /cerepro_resources/properties/cerepro.hr.backend/application-dev.properties ./src/main/resources/application-dev.properties"
                sh "cp /cerepro_resources/properties/cerepro.hr.backend/application-stage.properties ./src/main/resources/application-stage.properties"
                sh "cp /cerepro_resources/properties/cerepro.hr.backend/application-prod.properties ./src/main/resources/application-prod.properties"
                echo "Preparing artifact: ${ARTIFACT_FULL_FILE_NAME}"
                sh "./mvnw package -DskipTests"
                echo "Archiving artifact: ${ARTIFACT_FULL_FILE_NAME}"
                archiveArtifacts artifacts: "target/${ARTIFACT_FULL_FILE_NAME}", onlyIfSuccessful: true
                
            }
        } 
        stage ("DOCKERIZE APPLICATION") {
            steps {
                echo "Dockerizing application..."
                //sh "docker build -f Dockerfile -t centauriacademy/cerepro.hr.backend:${BUILD_NUMBER}_${BUILD_TIMESTAMP} ."
            }
        }
        stage ("DELIVERY ON DEV") {
            steps {
                echo "EXECUTING PRODUCTION ENVIRONEMNT PROMOTION"
                //sh "docker build -f Dockerfile -t centauriacademy/cerepro.hr.backend:${BUILD_NUMBER}_${BUILD_TIMESTAMP} ."
                //sh "/cerepro_resources/scp_put@env.sh ${PROMOTED_JOB_FULL_NAME} ${PROMOTED_ID} ${env.NAME} ${ARTIFACT_FULL_FILE_NAME} cerepro_resources ${DEV_ENVIRONMENT_HOSTNAME}"
                //sh "/cerepro_resources/delivery_on_docker_host.sh ${PROMOTED_JOB_FULL_NAME} ${PROMOTED_ID} ${ARTIFACT_FULL_FILE_NAME} cerepro_resources "
            }
        }
        stage ("DELIVERY ON PRODUCTION") {
            when { expression { return params.PROMOTE_ON_PRODUCTION } }
            steps {
                echo "EXECUTING PRODUCTION ENVIRONEMNT PROMOTION"
                //sh "docker build -f Dockerfile -t centauriacademy/cerepro.hr.backend:${BUILD_NUMBER}_${BUILD_TIMESTAMP} ."
                sh "/cerepro_resources/scp_put@env.sh ${PROMOTED_JOB_FULL_NAME} ${PROMOTED_ID} ${env.NAME} ${ARTIFACT_FULL_FILE_NAME} cerepro_resources ${DEV_ENVIRONMENT_HOSTNAME}"
            }
        } 
        
    }
    post {
		always {
			mail to: 'm.franco@proximanetwork.it',
			subject: "Completed Pipeline: ${currentBuild.fullDisplayName}",
			body: "Your build completed, please check: ${env.BUILD_URL}"
		}
	}
}