pipeline {
    // Executes on any available Jenkins agent
    agent any 

    // Links to the names of the tools configured in Jenkins UI
    tools {
        maven 'Maven-3' 
        jdk 'Java-21' 
    }

    stages {
        stage('Checkout Code') {
            steps {
                // Pulls the code from the Git repo connected to this job
                checkout scm 
            }
        }

        stage('Execute E2E Tests') {
            steps {
                // catchError ensures the pipeline doesn't instantly abort if tests fail,
                // allowing the 'post' section to run and generate your ExtentReports.
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    // Assuming you pass a headless flag and point to your suite
                    bat 'mvn clean test -DsuiteXmlFile=testng-customsuite.xml' 
                }
            }
        }
    }

    post {
        // The 'always' block runs regardless of test pass/fail status
        always {
            // Archiving TestNG default reports (Optional)
            junit 'target/surefire-reports/*.xml'
            
            // Publishing ExtentReports using HTML Publisher Plugin
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output', // Update this to your ExtentReport output directory
                reportFiles: 'ExtentReport.html', // Update this to your exact report file name
                reportName: 'Extent Automation Report',
                reportTitles: 'E2E Shopping Suite'
            ])
        }
    }
}
