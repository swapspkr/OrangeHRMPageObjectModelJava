pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        COMPOSE_PATH = "${WORKSPACE}/docker"
        SELENIUM_GRID = "true"
    }

    stages {

        stage('Start Selenium Grid via Docker Compose') {
            steps {
                script {
                    echo "Starting Selenium Grid with Docker Compose..."

                    if (isUnix()) {
                        sh """
                        docker compose -f ${COMPOSE_PATH}/docker-compose.yml up -d
                        """
                    } else {
                        bat """
                        docker compose -f ${COMPOSE_PATH}\\docker-compose.yml up -d
                        """
                    }

                    echo "Waiting for Selenium Grid to be ready..."
                    sleep 60
                }
            }
        }

        stage('Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/swapspkr/OrangeHRMPageObjectModelJava.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean install -DseleniumGrid=true'
                    } else {
                        bat 'mvn clean install -DseleniumGrid=true'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test -DseleniumGrid=true'
                    } else {
                        bat 'mvn test -DseleniumGrid=true'
                    }
                }
            }
        }

        stage('Stop Selenium Grid') {
            steps {
                script {
                    echo "Stopping Selenium Grid..."

                    if (isUnix()) {
                        sh "docker compose -f ${COMPOSE_PATH}/docker-compose.yml down"
                    } else {
                        bat "docker compose -f ${COMPOSE_PATH}\\docker-compose.yml down"
                    }
                }
            }
        }

        stage('Reports') {
            steps {
                publishHTML(target: [
                    reportDir: 'src/test/resources/ExtentReport',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Report'
                ])
            }
        }
    }

    post {

        always {
            archiveArtifacts artifacts: '**/src/test/resources/ExtentReport/*.html', fingerprint: true
            junit 'target/surefire-reports/*.xml'
        }

        success {
            emailext(
                to: 'swapspkr@gmail.com',
                subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                attachLog: true,
                body: """
                <html>
                <body>

                <p>Hello Team,</p>

                <p>The Jenkins build completed successfully.</p>

                <p><b>Project:</b> ${env.JOB_NAME}</p>
                <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                <p><b>Status:</b> <span style="color:green;"><b>SUCCESS</b></span></p>

                <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>

                <p><b>Extent Report:</b> 
                <a href="${env.BUILD_URL}HTML_20Extent_20Report/">View Report</a></p>

                <p>Regards,<br>Automation Team</p>

                </body>
                </html>
                """
            )
        }

        failure {
            emailext(
                to: 'swapspkr@gmail.com',
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                attachLog: true,
                body: """
                <html>
                <body>

                <p>Hello Team,</p>

                <p>The Jenkins build has <span style="color:red;"><b>FAILED</b></span>.</p>

                <p><b>Project:</b> ${env.JOB_NAME}</p>
                <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>

                <p><b>Build URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>

                <p>Please review the logs and fix the issue.</p>

                <p>Regards,<br>Automation Team</p>

                </body>
                </html>
                """
            )
        }
    }
}