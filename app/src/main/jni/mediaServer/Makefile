INCLUDES = -I../UsageEnvironment/include -I../groupsock/include -I../liveMedia/include -I../BasicUsageEnvironment/include
# Default library filename suffixes for each library that we link with.  The "config.*" file might redefine these later.
libliveMedia_LIB_SUFFIX = $(LIB_SUFFIX)
libBasicUsageEnvironment_LIB_SUFFIX = $(LIB_SUFFIX)
libUsageEnvironment_LIB_SUFFIX = $(LIB_SUFFIX)
libgroupsock_LIB_SUFFIX = $(LIB_SUFFIX)
##### Change the following for your environment:
#-------------------------------------------------------------------------------------------------
#
# 	Create BY zhang zhiwei 2014.07.03
# 	sunfrank2012@gmail.com
#
#-------------------------------------------------------------------------------------------------
# NDK ROOT
ANDROID_NDK_ROOT=/Users/gangzhang/Downloads/android-ndk-r10e
NDK_ROOT=/Users/gangzhang/Downloads/android-ndk-r10e
PREBUILT=/Users/gangzhang/Downloads/android-ndk-r10e/toolchains/arm-linux-androideabi-4.8/prebuilt/darwin-x86_64
STL_PATH=$(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi
PLATFORM=android-19

CROSS_COMPILE=		$(PREBUILT)/bin/arm-linux-androideabi-
COMPILE_OPTS =		$(INCLUDES) -fPIC -DANDROID  -mfloat-abi=softfp -I. -O2 -DXLOCALE_NOT_USED -DANDROID -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 --sysroot=${NDK_ROOT}/platforms/${PLATFORM}/arch-arm/  -I${ANDROID_NDK_ROOT}/platforms/${PLATFORM}/arch-arm/usr/include
C =			c
C_COMPILER =		$(CROSS_COMPILE)gcc
C_FLAGS =		$(COMPILE_OPTS)
CPP =			cpp
CPLUSPLUS_COMPILER =	$(CROSS_COMPILE)g++
CPLUSPLUS_FLAGS =	$(COMPILE_OPTS) -Wall -DBSD=1 -fexceptions -fno-rtti -nostdlib
OBJ =			o
LINK =			$(CROSS_COMPILE)g++ -o
LINK_OPTS = 		-nostdlib -lc -lm -lz -L$(STL_PATH) -lsupc++	-fno-rtti -fexceptions -L${ANDROID_NDK_ROOT}/platforms/${PLATFORM}/arch-arm/usr/lib -lstdc++ 

CONSOLE_LINK_OPTS =	$(LINK_OPTS)
LIBRARY_LINK =		$(CROSS_COMPILE)ar cr 
LIBRARY_LINK_OPTS =	
LIB_SUFFIX =			a
LIBS_FOR_CONSOLE_APPLICATION =
LIBS_FOR_GUI_APPLICATION =-nostdlib -lc -lm -lz -L$(STL_PATH) -lsupc++	-fno-rtti -fexceptions -L${ANDROID_NDK_ROOT}/platforms/${PLATFORM}/arch-arm/usr/lib -lstdc++ 
EXE =
##### End of variables to change

MEDIA_SERVER = live555MediaServer$(EXE)

PREFIX = /usr/local
ALL = $(MEDIA_SERVER)
all: $(ALL)

.$(C).$(OBJ):
	$(C_COMPILER) -c $(C_FLAGS) $<
.$(CPP).$(OBJ):
	$(CPLUSPLUS_COMPILER) -c $(CPLUSPLUS_FLAGS) $<

MEDIA_SERVER_OBJS = live555MediaServer.$(OBJ) DynamicRTSPServer.$(OBJ)

live555MediaServer.$(CPP):	DynamicRTSPServer.hh version.hh
DynamicRTSPServer.$(CPP):	DynamicRTSPServer.hh

USAGE_ENVIRONMENT_DIR = ../UsageEnvironment
USAGE_ENVIRONMENT_LIB = $(USAGE_ENVIRONMENT_DIR)/libUsageEnvironment.$(libUsageEnvironment_LIB_SUFFIX)
BASIC_USAGE_ENVIRONMENT_DIR = ../BasicUsageEnvironment
BASIC_USAGE_ENVIRONMENT_LIB = $(BASIC_USAGE_ENVIRONMENT_DIR)/libBasicUsageEnvironment.$(libBasicUsageEnvironment_LIB_SUFFIX)
LIVEMEDIA_DIR = ../liveMedia
LIVEMEDIA_LIB = $(LIVEMEDIA_DIR)/libliveMedia.$(libliveMedia_LIB_SUFFIX)
GROUPSOCK_DIR = ../groupsock
GROUPSOCK_LIB = $(GROUPSOCK_DIR)/libgroupsock.$(libgroupsock_LIB_SUFFIX)
LOCAL_LIBS =	$(LIVEMEDIA_LIB) $(GROUPSOCK_LIB) \
		$(BASIC_USAGE_ENVIRONMENT_LIB) $(USAGE_ENVIRONMENT_LIB)
LIBS =			$(LOCAL_LIBS) $(LIBS_FOR_CONSOLE_APPLICATION)

live555MediaServer$(EXE):	$(MEDIA_SERVER_OBJS) $(LOCAL_LIBS)
	$(LINK)$@ $(CONSOLE_LINK_OPTS) $(MEDIA_SERVER_OBJS) $(LIBS)

clean:
	-rm -rf *.$(OBJ) $(ALL) core *.core *~ include/*~

install: $(MEDIA_SERVER)
	  install -d $(DESTDIR)$(PREFIX)/bin
	  install -m 755 $(MEDIA_SERVER) $(DESTDIR)$(PREFIX)/bin

##### Any additional, platform-specific rules come here:
