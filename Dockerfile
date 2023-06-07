FROM maven:3.8.5-openjdk-17-slim AS build

WORKDIR /app

COPY ./pom.xml .

RUN mvn clean verify --fail-never

COPY ./src ./src

RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim

LABEL maintainer="stanis.stoyanov@gmail.com"
LABEL version="1.0.1"

RUN apt-get update && apt-get install -y curl python3 python3-pip && \
        python3 -m pip install --upgrade pip
RUN pip install numpy
RUN pip install sumy
RUN python3 -c "import nltk; nltk.download('punkt')" && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# Download latest yt-dlp binary
ADD "https://api.github.com/repos/yt-dlp/yt-dlp/releases?per_page=1" latest_release
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp
RUN chmod a+rx /usr/local/bin/yt-dlp

COPY --from=build /app/target/mooch-1.0.1.jar /usr/local/lib/mooch-1.0.1.jar

ENTRYPOINT ["java","-Xmx256m","-jar","/usr/local/lib/mooch-1.0.1.jar"]