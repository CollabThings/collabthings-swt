;--------------------------------

!define APPNAME "CollabThings"

; The name of the installer
Name "CollabThings"

; The file to write
OutFile "collabthings64.exe"

; The default installation directory
InstallDir $APPDATA\CollabThings

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
	File "files\assets\img\icon.ico"

	SetOutPath $INSTDIR\assets\ini
	File "files\assets\ini\settings.ini" 

	SetOutPath $INSTDIR\assets
	File "files\assets\config.xml" 
 
 	; Set output path to the installation directory.
	SetOutPath $INSTDIR
	 
	; Put file there
	File "files\CollabThings.exe" 
	File "files\Ionic.Zip.dll"
 	File "files\log4net.dll"
 
	# Start Menu
	createDirectory "$SMPROGRAMS\${APPNAME}"
	createShortCut "$SMPROGRAMS\${APPNAME}\${APPNAME}.lnk" "$INSTDIR\collabthings.exe" "" "$INSTDIR\assets\img\icon.ico" 
SectionEnd ; end the section

