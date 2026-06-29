NAME = mail-stripper

NI_TAG = ghcr.io/graalvm/native-image:22.2.0

NI_ARGS = \
	--initialize-at-build-time \
	--report-unsupported-elements-at-runtime \
	--no-fallback \
	--no-server \
	-jar ${JAR} \
	-J-Dfile.encoding=UTF-8 \
	--enable-url-protocols=http,https \
	-H:+PrintClassInitialization \
	-H:+ReportExceptionStackTraces \
	-H:Log=registerResource \
	--initialize-at-run-time=com.mysql.cj.jdbc.AbandonedConnectionCleanupThread \
	--initialize-at-run-time=com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.AbandonedConnectionCleanupThread \
	--initialize-at-run-time=com.mysql.cj.jdbc.Driver \
	--initialize-at-run-time=com.mysql.cj.jdbc.NonRegisteringDriver \
	-H:ReflectionConfigurationFiles=reflection-config.json \
	-H:Name=./builds/${NAME}-


PLATFORM = PLATFORM

JAR = target/uberjar/${NAME}.jar

DATE = $(shell date +%s)


platform-docker:
	docker run -it --rm --entrypoint /bin/sh ${NI_TAG} -c 'echo `uname -s`-`uname -m`' > ${PLATFORM}

build-binary-docker: uberjar platform-docker
	docker run -it --rm -v ${PWD}:/build -w /build ${NI_TAG} ${NI_ARGS}$(shell cat ${PLATFORM})

platform-local:
	echo `uname -s`-`uname -m` > ${PLATFORM}

graal-build: platform-local
	native-image ${NI_ARGS}$(shell cat ${PLATFORM})

build-binary-local: uberjar graal-build

