;--------------------------------

!define APPNAME "Library of Things"

; The name of the installer
Name "Library if Things"

; The file to write
OutFile "libraryofthing64.exe"

; The default installation directory
InstallDir $APPDATA\LibraryOfThings

; Request application privileges for Windows Vista
RequestExecutionLevel user

;--------------------------------

; Pages

;Page directory
;Page instfiles

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important	
	SetOutPath $INSTDIR\assets\img
	File "files\assets\img\logo.png" 
	File "files\assets\img\splash.png" 
	File "files\assets\img\icon.ico" " 
	File "files\assets\config.xml" 
	 
	SetOutPath $INSTDIR\assets\ini
	File "files\assets\ini\settings.ini" 
 
 	; Set output path to the installation directory.
	SetOutPath $INSTDIR
	 
	; Put file there
	File "files\LibraryOfThings.exe" 
	File "files\Ionic.Zip.dll"
 	File "files\log4net.dll"
 
	# Start Menu
	createDirectory "$SMPROGRAMS\${APPNAME}"
	createShortCut "$SMPROGRAMS\${APPNAME}\${APPNAME}.lnk" "$INSTDIR\libraryofthings.exe" "" "$INSTDIR\assets\img\icon.ico" 
SectionEnd ; end the section

