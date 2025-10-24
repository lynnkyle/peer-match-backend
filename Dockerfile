# 依赖的基础镜像
FROM maven:3.5-jdk-8-alpine AS builder

# 复制代码到镜像
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Maven打包项目
RUN mvn package -DskipTests

# 运行镜像默认命令
CMD ["java","-jar","/app/target/peer-match-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]