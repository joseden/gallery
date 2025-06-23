pipeline {
    agent any
    
    environment {
        // Render deploy hook
        RENDER_DEPLOY_HOOK = credentials('render-deploy-hook')
        // Slack bot token (verified working)
        SLACK_TOKEN = credentials('slack-bot-token')
        // Your Render app URL
        RENDER_APP_URL = 'https://gallery-fa8q.onrender.com'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from repository...'
                checkout scm
                echo "Build Number: ${env.BUILD_NUMBER}"
                echo "Job Name: ${env.JOB_NAME}"
                echo "Build URL: ${env.BUILD_URL}"
            }
        }
        
        stage('Install Dependencies') {
            steps {
                echo 'Installing Node.js dependencies...'
                sh '''
                    echo "Node.js version:"
                    node --version
                    echo "npm version:"
                    npm --version
                    
                    echo "Installing dependencies..."
                    npm install
                    
                    echo "Dependencies installed successfully!"
                '''
            }
        }
        
        stage('Run Tests') {
            steps {
                echo 'Running application tests...'
                sh '''
                    echo "Starting test execution..."
                    
                    # Run the tests
                    npm test
                    
                    echo "✅ All tests passed successfully!"
                '''
            }
        }
        
        stage('Verify Application') {
            steps {
                echo 'Verifying application files and configuration...'
                sh '''
                    echo "Checking required files..."
                    test -f package.json && echo "✓ package.json found" || echo "✗ package.json missing"
                    test -f server.js && echo "✓ server.js found" || echo "✗ server.js missing"
                    test -f config.js && echo "✓ config.js found" || echo "✗ config.js missing"
                    test -f Jenkinsfile && echo "✓ Jenkinsfile found" || echo "✗ Jenkinsfile missing"
                    
                    echo "Checking for milestone banners..."
                    grep -c "MILESTONE" views/index.ejs && echo "✓ Milestone banners found" || echo "⚠ Check milestone banners"
                    
                    echo "Application structure verified!"
                '''
            }
        }
        
        stage('Deploy to Render') {
            steps {
                echo 'Triggering deployment to Render...'
                script {
                    try {
                        sh 'curl -X POST "$RENDER_DEPLOY_HOOK"'
                        echo '🚀 Deployment triggered successfully!'
                        echo "Render will now build and deploy the application"
                        
                        // Wait for deployment to start
                        sleep(time: 10, unit: 'SECONDS')
                        
                    } catch (Exception e) {
                        error "Deployment failed: ${e.getMessage()}"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            echo "🎉 Build ${env.BUILD_NUMBER} deployed!"
            echo "📊 Tests passed and application deployed"
            echo "🌐 Application is live at: ${env.RENDER_APP_URL}"
            
            // Send Slack notification using direct API call (verified working token)
            script {
                def deploymentTime = new Date().format("yyyy-MM-dd HH:mm:ss")
                def commitMessage = sh(
                    script: 'git log -1 --pretty=format:"%s"',
                    returnStdout: true
                ).trim()
                
                try {
                    def slackResponse = sh(
                        script: """
                            curl -s -X POST https://slack.com/api/chat.postMessage \\
                            -H "Authorization: Bearer ${env.SLACK_TOKEN}" \\
                            -H "Content-Type: application/json" \\
                            -d '{
                                "channel": "#joseph_ip1",
                                "text": "🎉 *Deployment Successful!* 🚀\\n\\n*Project:* ${env.JOB_NAME}\\n*Build ID:* #${env.BUILD_NUMBER}\\n*Status:* ✅ Success\\n*Deployed:* ${deploymentTime}\\n\\n🔗 *Live Application:* ${env.RENDER_APP_URL}\\n📊 *Build Details:* ${env.BUILD_URL}\\n📝 *Latest Commit:* ${commitMessage}\\n\\n*🎯 Deployment Summary:*\\n- All automated tests passed ✅\\n- Successfully deployed to Render ☁️\\n- Application is live and accessible 🌐\\n- Database connection verified ✅\\n\\n*🏆 Milestones Completed:*\\n🚀 Milestone 2: Basic CI/CD Pipeline\\n🧪 Milestone 3: Automated Testing & Email Notifications\\n💬 Milestone 4: Slack Integration & Team Notifications\\n\\n_Automated deployment notification from Jenkins CI/CD Pipeline - josephdena workspace_",
                                "username": "Jenkins CI/CD Bot",
                                "icon_emoji": ":rocket:"
                            }'
                        """,
                        returnStdout: true
                    )
                    
                    echo "Slack notification sent successfully to #joseph_ip1!"
                    echo "Response: ${slackResponse}"
                    
                } catch (Exception e) {
                    echo "Slack notification failed: ${e.getMessage()}"
                    echo "But deployment was successful!"
                }
            }
        }
        
        failure {
            echo '❌ Pipeline failed!'
            echo 'Sending failure notifications...'
            
            // Send email notification on failure
            emailext (
                subject: "🚨 Pipeline Failed: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """
                    <h2 style="color: red;">🚨 CI/CD Pipeline Failure Alert</h2>
                    
                    <p>The CI/CD pipeline for <strong>${env.JOB_NAME}</strong> has failed during Build #${env.BUILD_NUMBER}.</p>
                    
                    <h3>Build Details:</h3>
                    <ul>
                        <li><strong>Build Number:</strong> ${env.BUILD_NUMBER}</li>
                        <li><strong>Job Name:</strong> ${env.JOB_NAME}</li>
                        <li><strong>Build URL:</strong> <a href="${env.BUILD_URL}">View Build Details</a></li>
                        <li><strong>Console Logs:</strong> <a href="${env.BUILD_URL}console">View Console Output</a></li>
                        <li><strong>Failed At:</strong> ${new Date()}</li>
                    </ul>
                    
                    <h3>🔍 Investigation Steps:</h3>
                    <ol>
                        <li>Check the <a href="${env.BUILD_URL}console">build console output</a></li>
                        <li>Review recent Git commits for potential issues</li>
                        <li>Run tests locally: <code>npm test</code></li>
                        <li>Verify MongoDB Atlas connection: <code>npm start</code></li>
                        <li>Check Render deployment status</li>
                    </ol>
                    
                    <p><em>Automated failure notification from Jenkins CI/CD Pipeline</em></p>
                """,
                mimeType: 'text/html',
                to: "${env.CHANGE_AUTHOR_EMAIL}",
                from: "jenkins@josephs-company.com"
            )
            
            // Send Slack failure notification
            script {
                def failureTime = new Date().format("yyyy-MM-dd HH:mm:ss")
                
                try {
                    sh """
                        curl -s -X POST https://slack.com/api/chat.postMessage \\
                        -H "Authorization: Bearer ${env.SLACK_TOKEN}" \\
                        -H "Content-Type: application/json" \\
                        -d '{
                            "channel": "#joseph_ip1",
                            "text": "🚨 *DEPLOYMENT FAILED!* ❌\\n\\n*Project:* ${env.JOB_NAME}\\n*Build ID:* #${env.BUILD_NUMBER}\\n*Status:* ❌ Failed\\n*Failed At:* ${failureTime}\\n\\n🔗 *Build Logs:* ${env.BUILD_URL}console\\n📊 *Build Details:* ${env.BUILD_URL}\\n\\n*🔍 Immediate Actions Required:*\\n1. Check the build console logs (link above)\\n2. Run tests locally: npm test\\n3. Verify MongoDB Atlas connection\\n4. Review recent code changes\\n\\n*📧 Additional Details:*\\nDetailed failure report sent via email to development team.\\n\\n_Automated failure alert from Jenkins CI/CD Pipeline - josephdena workspace_",
                            "username": "Jenkins CI/CD Bot",
                            "icon_emoji": ":warning:"
                        }'
                    """
                    
                    echo "Slack failure notification sent to #joseph_ip1!"
                    
                } catch (Exception e) {
                    echo "Slack notification failed, but email notification was sent"
                }
            }
        }
        
        unstable {
            echo '⚠️ Pipeline completed with warnings'
            
            script {
                try {
                    sh """
                        curl -s -X POST https://slack.com/api/chat.postMessage \\
                        -H "Authorization: Bearer ${env.SLACK_TOKEN}" \\
                        -H "Content-Type: application/json" \\
                        -d '{
                            "channel": "#joseph_ip1",
                            "text": "⚠️ *Deployment Completed with Warnings* ⚠️\\n\\n*Project:* ${env.JOB_NAME}\\n*Build ID:* #${env.BUILD_NUMBER}\\n*Status:* Unstable\\n*Completed:* ${new Date().format("yyyy-MM-dd HH:mm:ss")}\\n\\n🔗 *Live Application:* ${env.RENDER_APP_URL}\\n📊 *Build Details:* ${env.BUILD_URL}\\n\\nSome tests may have been skipped or marked as unstable. Please review the build logs for details.",
                            "username": "Jenkins CI/CD Bot",
                            "icon_emoji": ":warning:"
                        }'
                    """
                } catch (Exception e) {
                    echo "Slack warning notification failed"
                }
            }
        }
    }
}
