pipeline {
    agent any
    
    tools {
        nodejs 'NodeJS_20'
    }

    environment {
        // --- 1. Cấu hình cho Backend (jira_automation.js) ---
        JIRA_API_TOKEN          = credentials('JIRA_API_TOKEN')
        JIRA_DOMAIN             = 'caongoctanvo.atlassian.net'
        JIRA_EMAIL              = 'caongoctanvo@gmail.com'
        JIRA_PROJECT_KEY        = 'TAP'
        JIRA_SERVICE_ISSUE_TYPE = 'Epic'
        JIRA_AUTOMATION_ISSUE_TYPE = 'Task'
        JIRA_BUG_ISSUE_TYPE     = 'Subbug'
        POSTMAN_COLLECTION_ID   = '55110231-c5612bea-0c70-4885-8563-a2a269fd1756'
        API_BASE_URL            = 'http://gateway:8080/api'

        // --- 2. Cấu hình cho Frontend (config.js / service.js) ---
        JIRA_BASE               = 'https://caongoctanvo.atlassian.net'
        EMAIL                   = 'caongoctanvo@gmail.com'
        TOKEN                   = credentials('JIRA_API_TOKEN')
        PROJECT_KEY             = 'TAP'
        JIRA_MODE               = 'jira'
        HEADLESS                = 'true'

        WEB_URL                 = 'http://host.docker.internal:3000'
        API_URL                 = 'http://gateway:8080'

        // --- 3. Cấu hình Google Cloud (GCP) ---
        GCP_PROJECT_ID      = 'ev-project-500309'
        GCP_REGION          = 'asia-southeast1'
        ARTIFACT_REPO       = 'ev-repo'
        
        // Sửa lại thành chuỗi bình thường (KHÔNG dùng hàm credentials ở đây)
        GCP_CREDENTIALS_ID  = 'GCP_CREDENTIALS' 

        CLOUD_SQL_IP        = '34.177.103.187'
        DB_USER             = 'evuser'
        DB_PASS             = 'Ev_station_2026'
    }

    triggers {
        // Đúng 00:00 (Múi giờ UTC) chạy Full Regression Test
        cron('0 0 * * *')
        // Quét code mới mỗi 2 tiếng
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('1. Kéo Code Từ Git') {
            steps {
                echo '📥 Đang kéo phiên bản code mới nhất từ kho lưu trữ...'
                checkout scm
            }
        }

        stage('2. Cài Đặt Môi Trường') {
            steps {
                echo '📦 Cài đặt thư viện Backend Test...'
                dir('backend/test') {
                    sh 'npm install newman dotenv axios'
                }

                echo '📦 Cài đặt thư viện Frontend UI Test...'
                dir('tool-test/codecept-jira-automation') {
                    sh 'npm install'
                    sh 'npx playwright install chromium --with-deps'
                }
            }
        }

        stage('3. Chạy Automation Test & Đồng Bộ Jira') {
            steps {
                script {
                    // --- ĐỊNH NGHĨA HÀM CHẠY BACKEND CHO GỌN ---
                    def runBE = { service ->
                        dir('backend/test') {
                            if (service == "ALL") {
                                sh 'node jira_automation.js'
                            } else {
                                sh "node jira_automation.js \"${service}\""
                            }
                        }
                    }

                    // --- ĐỊNH NGHĨA HÀM CHẠY FRONTEND CHO GỌN ---
                    def runFE = { pattern ->
                        dir('tool-test/codecept-jira-automation') {
                            if (pattern == "ALL") {
                                sh 'node run.js --mode=jira'
                            } else {
                                sh "node run.js --mode=jira --file=${pattern} --workers=1"
                            }
                        }
                    }

                    // --- ĐỊNH NGHĨA HÀM DEPLOY LÊN GOOGLE CLOUD ---
                    def deployGCP = { serviceName, port, dbName ->
                        echo "☁️ Đóng gói và Deploy [${serviceName}] lên Google Cloud Run..."
                        def imageTag = "${GCP_REGION}-docker.pkg.dev/${GCP_PROJECT_ID}/${ARTIFACT_REPO}/${serviceName}:latest"

                        // SỬA ĐÚNG DÒNG NÀY (Thêm chữ backend/ vào trước)
                        dir("backend/${serviceName}") {
                            sh "docker build -t ${imageTag} ."

                            withCredentials([file(credentialsId: "${GCP_CREDENTIALS_ID}", variable: 'GC_KEY')]) {
                                sh "gcloud auth activate-service-account --key-file=\${GC_KEY}"
                                sh "gcloud auth configure-docker ${GCP_REGION}-docker.pkg.dev --quiet"
                                sh "docker push ${imageTag}"

                                def envVars = "DB_HOST=${CLOUD_SQL_IP},DB_PORT=3306,DB_NAME=${dbName},DB_USER=${DB_USER},DB_PASS=${DB_PASS}"

                                sh """
                                    gcloud run deploy ${serviceName} \\
                                    --image ${imageTag} \\
                                    --region ${GCP_REGION} \\
                                    --project ${GCP_PROJECT_ID} \\
                                    --port ${port} \\
                                    --set-env-vars ${envVars} \\
                                    --allow-unauthenticated \\
                                    --quiet
                                """
                            }
                        }
                    }

                    def isTimer = currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause')

                    if (isTimer) {
                        echo '🕒 Chạy theo lịch 00:00 -> Đang test TOÀN BỘ hệ thống (BE + FE)...'
                        runBE("ALL")
                        runFE("ALL")
                    } else {
                        def commitMsg = sh(script: 'git log -1 --pretty=%B', returnStdout: true).trim().toLowerCase()
                        echo "💻 Lời nhắn commit: ${commitMsg}"

                        // ================= MODULE STATION =================
                        if (commitMsg.contains('[be-station]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: station-service'
                            runBE("station-service")
                            deployGCP("station-service", "8084", "station_service")
                        }
                        else if (commitMsg.contains('[fe-station]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: station'
                            runFE("station/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-station]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend station-service + Frontend UI station'
                            runBE("station-service")
                            runFE("station/**/*_test.js")
                            deployGCP("station-service", "8084", "station_service")
                        }

                        // ================= MODULE USERS =================
                        else if (commitMsg.contains('[be-users]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: users-service'
                            runBE("users-service")
                            deployGCP("user-service", "8081", "user_service")
                        }
                        else if (commitMsg.contains('[fe-users]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: users'
                            runFE("users/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-users]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend users-service + Frontend UI users'
                            runBE("users-service")
                            runFE("users/**/*_test.js")
                            deployGCP("user-service", "8081", "user_service")
                        }

                        // ================= MODULE VEHICLE =================
                        else if (commitMsg.contains('[be-vehicle]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: vehicle-service'
                            runBE("vehicle-service")
                            deployGCP("vehicle-service", "8088", "vehicle_service")
                        }
                        else if (commitMsg.contains('[fe-vehicle]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: vehicle'
                            runFE("vehicle/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-vehicle]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend vehicle-service + Frontend UI vehicle'
                            runBE("vehicle-service")
                            runFE("vehicle/**/*_test.js")
                            deployGCP("vehicle-service", "8088", "vehicle_service")
                        }

                        // ================= MODULE TRANSACTION =================
                        else if (commitMsg.contains('[be-transaction]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: transaction-service'
                            runBE("transaction-service")
                            deployGCP("transaction-service", "8085", "transaction_service")
                        }
                        else if (commitMsg.contains('[fe-transaction]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: transaction'
                            runFE("transaction/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-transaction]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend transaction-service + Frontend UI transaction'
                            runBE("transaction-service")
                            runFE("transaction/**/*_test.js")
                            deployGCP("transaction-service", "8085", "transaction_service")
                        }

                        // ================= MODULE BATTERY =================
                        else if (commitMsg.contains('[be-battery]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: battery-service'
                            runBE("battery-service")
                            deployGCP("battery-service", "8083", "battery_service")
                        }
                        else if (commitMsg.contains('[fe-battery]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: battery'
                            runFE("battery/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-battery]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend battery-service + Frontend UI battery'
                            runBE("battery-service")
                            runFE("battery/**/*_test.js")
                            deployGCP("battery-service", "8083", "battery_service")
                        }

                        // ================= MODULE FEEDBACK =================
                        else if (commitMsg.contains('[be-feedback]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: feedback-service'
                            runBE("feedback-service")
                            deployGCP("feedback-service", "8086", "feedback_service")
                        }
                        else if (commitMsg.contains('[fe-feedback]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: feedback'
                            runFE("feedback/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-feedback]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend feedback-service + Frontend UI feedback'
                            runBE("feedback-service")
                            runFE("feedback/**/*_test.js")
                            deployGCP("feedback-service", "8086", "feedback_service")
                        }

                        // ================= MODULE SUBSCRIPTION =================
                        else if (commitMsg.contains('[be-subscription]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Backend: subscription-service'
                            runBE("subscription-service")
                            deployGCP("subscription-service", "8082", "subscription_service")
                        }
                        else if (commitMsg.contains('[fe-subscription]')) {
                            echo '🚀 Bắt đầu test riêng lẻ Frontend UI: subscription'
                            runFE("subscription/**/*_test.js")
                        }
                        else if (commitMsg.contains('[fix-subscription]')) {
                            echo '🚀 Bắt đầu test luồng FIX: Backend subscription-service + Frontend UI subscription'
                            runBE("subscription-service")
                            runFE("subscription/**/*_test.js")
                            deployGCP("subscription-service", "8082", "subscription_service")
                        }

                        // ================= MẶC ĐỊNH (FULL) =================
                        else {
                            echo '⚠️ Không tìm thấy [tag] chỉ định trong commit -> Test TOÀN BỘ hệ thống (BE + FE) cho an toàn.'
                            runBE("ALL")
                            runFE("ALL")
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo '🏁 Tiến trình CI/CD hoàn tất, tự động dọn dẹp môi trường và thu thập hình ảnh lỗi (nếu có).'
            archiveArtifacts artifacts: 'tool-test/codecept-jira-automation/output/*.png', allowEmptyArchive: true
        }
        success {
            echo '✅ Tuyệt vời! Tất cả các ca kiểm thử đều Pass. Không có Bug mới.'
        }
        failure {
            echo '⚠️ Phát hiện lỗi (Bug). Hệ thống đã tự động đẩy/cập nhật thông tin lên thẻ Jira cho Dev.'
        }
    }
}