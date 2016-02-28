INCLUDES = -Iinclude -I../UsageEnvironment/include -I../groupsock/include
PREFIX = /usr/local
LIBDIR = $(PREFIX)/lib
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

NAME = libBasicUsageEnvironment
LIB_FILE = $(NAME).$(LIB_SUFFIX)
ALL = $(LIB_FILE)
all:	$(ALL)

OBJS = BasicUsageEnvironment0.$(OBJ) BasicUsageEnvironment.$(OBJ) \
	BasicTaskScheduler0.$(OBJ) BasicTaskScheduler.$(OBJ) \
	DelayQueue.$(OBJ) BasicHashTable.$(OBJ)

libBasicUsageEnvironment.$(LIB_SUFFIX): $(OBJS)
	$(LIBRARY_LINK)$@ $(LIBRARY_LINK_OPTS) \
		$(OBJS)

.$(C).$(OBJ):
	$(C_COMPILER) -c $(C_FLAGS) $<       

.$(CPP).$(OBJ):
	$(CPLUSPLUS_COMPILER) -c $(CPLUSPLUS_FLAGS) $<

BasicUsageEnvironment0.$(CPP):	include/BasicUsageEnvironment0.hh
include/BasicUsageEnvironment0.hh:	include/BasicUsageEnvironment_version.hh include/DelayQueue.hh
BasicUsageEnvironment.$(CPP):	include/BasicUsageEnvironment.hh
include/BasicUsageEnvironment.hh:	include/BasicUsageEnvironment0.hh
BasicTaskScheduler0.$(CPP):	include/BasicUsageEnvironment0.hh include/HandlerSet.hh
BasicTaskScheduler.$(CPP):	include/BasicUsageEnvironment.hh include/HandlerSet.hh
DelayQueue.$(CPP):		include/DelayQueue.hh
BasicHashTable.$(CPP):		include/BasicHashTable.hh

clean:
	-rm -rf *.$(OBJ) $(ALL) core *.core *~ include/*~

install: install1 $(INSTALL2)
install1: libBasicUsageEnvironment.$(LIB_SUFFIX)
	  install -d $(DESTDIR)$(PREFIX)/include/BasicUsageEnvironment $(DESTDIR)$(LIBDIR)
	  install -m 644 include/*.hh $(DESTDIR)$(PREFIX)/include/BasicUsageEnvironment
	  install -m 644 libBasicUsageEnvironment.$(LIB_SUFFIX) $(DESTDIR)$(LIBDIR)
install_shared_libraries: libBasicUsageEnvironment.$(LIB_SUFFIX)
	  ln -fs $(NAME).$(LIB_SUFFIX) $(DESTDIR)$(LIBDIR)/$(NAME).$(SHORT_LIB_SUFFIX)
	  ln -fs $(NAME).$(LIB_SUFFIX) $(DESTDIR)$(LIBDIR)/$(NAME).so

##### Any additional, platform-specific rules come here:
