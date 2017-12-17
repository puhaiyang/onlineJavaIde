/**
 * @author: haiyangp
 * @date: 2017/12/17
 * desc: 将项目部署到阿里云服务器脚本
 */
node {
    def HOST_PWD = "your_host_password,eg:123456"
    def HOST_USERNAME = "your_host_user,eg:root"
    def HOST_NAME = "your_host_name,eg:192.168.1.103"

    stage('get clone') {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], gitTool: 'Default', submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/puhaiyang/onlineJavaIde.git']]])
    }

    stage('mvn test') {
        withMaven(maven: 'M3') {
            sh "mvn test"
        }
    }

    stage('mvn install') {
        withMaven(maven: 'M3') {
            sh "mvn install"
        }
    }

    stage('depoly') {
        sh 'sshpass -p  ' + HOST_PWD + ' ssh  -o StrictHostKeyChecking=no ' + HOST_USERNAME + '@' + HOST_NAME + ' ls'
        sh 'sshpass -p  ' + HOST_PWD + ' scp  -o StrictHostKeyChecking=no  target/*.jar ' + HOST_USERNAME + '@' + HOST_NAME + ':.'
        echo 'haha'
    }
    //    -p:指定ssh的密码
    //    -o StrictHostKeyChecking=no 避免第一次登录出现公钥检查。也就是避免出现


    stage('restart') {
        timeout(time: 1, unit: 'DAYS') {
            input message: 'Approve deployment?', submitter: 'it-ops'
        }
    }

}