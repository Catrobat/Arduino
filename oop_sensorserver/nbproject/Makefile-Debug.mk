#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=avr-gcc
CCC=avr-g++
CXX=avr-g++
FC=gfortran
AS=avr-as

# Macros
CND_PLATFORM=arduino-Linux-x86
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/1113415871/Dhcp.o \
	${OBJECTDIR}/_ext/1113415871/Dns.o \
	${OBJECTDIR}/_ext/1113415871/Ethernet.o \
	${OBJECTDIR}/_ext/1113415871/EthernetClient.o \
	${OBJECTDIR}/_ext/1113415871/EthernetServer.o \
	${OBJECTDIR}/_ext/1113415871/EthernetUdp.o \
	${OBJECTDIR}/_ext/376897508/socket.o \
	${OBJECTDIR}/_ext/376897508/w5100.o \
	${OBJECTDIR}/_ext/1847204534/PN532.o \
	${OBJECTDIR}/_ext/1847204534/emulatetag.o \
	${OBJECTDIR}/_ext/1847204534/llcp.o \
	${OBJECTDIR}/_ext/1847204534/mac_link.o \
	${OBJECTDIR}/_ext/1847204534/snep.o \
	${OBJECTDIR}/_ext/1230331625/PN532_SPI.o \
	${OBJECTDIR}/_ext/667793348/SPI.o \
	${OBJECTDIR}/_ext/1887368547/SoftwareSerial.o \
	${OBJECTDIR}/main.o


# C Compiler Flags
CFLAGS=-Os -Wall -ffunction-sections -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000L -DARDUINO=101

# CC Compiler Flags
CCFLAGS=-Os -Wall -fno-exceptions -ffunction-sections -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000L -DARDUINO=101
CXXFLAGS=-Os -Wall -fno-exceptions -ffunction-sections -fdata-sections -mmcu=atmega2560 -DF_CPU=16000000L -DARDUINO=101

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L../ -larduinomega_corelib -lm

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/oop_sensorserver

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/oop_sensorserver: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/oop_sensorserver ${OBJECTFILES} ${LDLIBSOPTIONS} -Os -Wl,--gc-sections -mmcu=atmega2560

${OBJECTDIR}/_ext/1113415871/Dhcp.o: /opt/arduino/libraries/Ethernet/Dhcp.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1113415871
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1113415871/Dhcp.o /opt/arduino/libraries/Ethernet/Dhcp.cpp

${OBJECTDIR}/_ext/1113415871/Dns.o: /opt/arduino/libraries/Ethernet/Dns.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1113415871
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1113415871/Dns.o /opt/arduino/libraries/Ethernet/Dns.cpp

${OBJECTDIR}/_ext/1113415871/Ethernet.o: /opt/arduino/libraries/Ethernet/Ethernet.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1113415871
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1113415871/Ethernet.o /opt/arduino/libraries/Ethernet/Ethernet.cpp

${OBJECTDIR}/_ext/1113415871/EthernetClient.o: /opt/arduino/libraries/Ethernet/EthernetClient.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1113415871
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1113415871/EthernetClient.o /opt/arduino/libraries/Ethernet/EthernetClient.cpp

${OBJECTDIR}/_ext/1113415871/EthernetServer.o: /opt/arduino/libraries/Ethernet/EthernetServer.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1113415871
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1113415871/EthernetServer.o /opt/arduino/libraries/Ethernet/EthernetServer.cpp

${OBJECTDIR}/_ext/1113415871/EthernetUdp.o: /opt/arduino/libraries/Ethernet/EthernetUdp.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1113415871
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1113415871/EthernetUdp.o /opt/arduino/libraries/Ethernet/EthernetUdp.cpp

${OBJECTDIR}/_ext/376897508/socket.o: /opt/arduino/libraries/Ethernet/utility/socket.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/376897508
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/376897508/socket.o /opt/arduino/libraries/Ethernet/utility/socket.cpp

${OBJECTDIR}/_ext/376897508/w5100.o: /opt/arduino/libraries/Ethernet/utility/w5100.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/376897508
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/376897508/w5100.o /opt/arduino/libraries/Ethernet/utility/w5100.cpp

${OBJECTDIR}/_ext/1847204534/PN532.o: /opt/arduino/libraries/NFC/PN532/PN532.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1847204534
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1847204534/PN532.o /opt/arduino/libraries/NFC/PN532/PN532.cpp

${OBJECTDIR}/_ext/1847204534/emulatetag.o: /opt/arduino/libraries/NFC/PN532/emulatetag.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1847204534
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1847204534/emulatetag.o /opt/arduino/libraries/NFC/PN532/emulatetag.cpp

${OBJECTDIR}/_ext/1847204534/llcp.o: /opt/arduino/libraries/NFC/PN532/llcp.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1847204534
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1847204534/llcp.o /opt/arduino/libraries/NFC/PN532/llcp.cpp

${OBJECTDIR}/_ext/1847204534/mac_link.o: /opt/arduino/libraries/NFC/PN532/mac_link.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1847204534
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1847204534/mac_link.o /opt/arduino/libraries/NFC/PN532/mac_link.cpp

${OBJECTDIR}/_ext/1847204534/snep.o: /opt/arduino/libraries/NFC/PN532/snep.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1847204534
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1847204534/snep.o /opt/arduino/libraries/NFC/PN532/snep.cpp

${OBJECTDIR}/_ext/1230331625/PN532_SPI.o: /opt/arduino/libraries/NFC/PN532_SPI/PN532_SPI.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1230331625
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1230331625/PN532_SPI.o /opt/arduino/libraries/NFC/PN532_SPI/PN532_SPI.cpp

${OBJECTDIR}/_ext/667793348/SPI.o: /opt/arduino/libraries/SPI/SPI.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/667793348
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/667793348/SPI.o /opt/arduino/libraries/SPI/SPI.cpp

${OBJECTDIR}/_ext/1887368547/SoftwareSerial.o: /opt/arduino/libraries/SoftwareSerial/SoftwareSerial.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/1887368547
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/_ext/1887368547/SoftwareSerial.o /opt/arduino/libraries/SoftwareSerial/SoftwareSerial.cpp

${OBJECTDIR}/main.o: main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -I/opt/arduino/hardware/arduino/cores/arduino -I/opt/arduino/libraries/SoftwareSerial -I/opt/arduino/libraries/SPI -I/opt/arduino/libraries/Ethernet -I/opt/arduino/libraries/Ethernet/utility -I/opt/arduino/libraries/PN532 -I/opt/arduino/libraries/PN532_SPI -std=c++11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/main.o main.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/oop_sensorserver

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
