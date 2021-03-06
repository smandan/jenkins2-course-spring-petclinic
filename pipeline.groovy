node {

    notify("Started")

    try {
        stage('checkout') {

            git 'https://github.com/smandan/jenkins2-course-spring-petclinic.git'
        }  
        
        stage('compiling, testing,packaging, and verifying"') {

            sh 'mvn clean verify'
        }

    notify("Completed")
    
    } catch (err) {
      notify("Error ${err}")
        echo "Caught: ${err}"
        currentBuild.result = 'Failure'
    }
    
    notify("Succeeded")

    stage('archival') {
        step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/TEST-*.xml'])

        archiveArtifacts artifacts: "target/*.?ar", followSymlinks: false

        publishHTML(target: [allowMissing: true,
                     alwaysLinkToLastBuild: false,
                     escapeUnderscores: false,
                     keepAll: true,
                     reportDir: 'target/site/jacoco/',
                     reportFiles: 'index.html',
                     reportName: 'Code-Coverage',
                     reportTitles: ''])
    }
}

def notify(status){
    emailext (
      to: "mandas1@nationwide.com",
      subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}
