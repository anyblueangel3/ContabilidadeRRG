; Script de instalação do sistema ContabilidadeRRG

[Setup]
AppName=ContabilidadeRRG
AppVersion=1.0
DefaultDirName={autopf}\ContabilidadeRRG
DefaultGroupName=ContabilidadeRRG
OutputBaseFilename=InstaladorBase-ContabilidadeRRG
Compression=lzma
SolidCompression=yes
ArchitecturesInstallIn64BitMode=x64compatible
PrivilegesRequired=admin

[Languages]
Name: "pt"; MessagesFile: "compiler:Languages\Portuguese.isl"

[Files]
; JAR principal
Source: "contabilidade-1.0-SNAPSHOT.jar"; DestDir: "{app}"; Flags: ignoreversion

; JRE Liberica
Source: "bellsoft-jre21.0.7+9-windows-amd64.zip"; DestDir: "{tmp}"; Flags: ignoreversion

; MySQL
Source: "mysql-8.0.28-winx64.zip"; DestDir: "{tmp}"; Flags: ignoreversion

; 7-Zip CLI
Source: "7zip\7za.exe"; DestDir: "{tmp}"; Flags: ignoreversion
Source: "7zip\7za.dll"; DestDir: "{tmp}"; Flags: ignoreversion

; Script de instalação e configuração do MySQL com porta dinâmica iniciando em 3310
Source: "instalador_mysql_3310.bat"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\ContabilidadeRRG"; Filename: "{app}\jre-21.0.7\bin\javaw.exe"; Parameters: "-jar ""{app}\contabilidade-1.0-SNAPSHOT.jar"""
Name: "{commondesktop}\ContabilidadeRRG"; Filename: "{app}\jre-21.0.7\bin\javaw.exe"; Parameters: "-jar ""{app}\contabilidade-1.0-SNAPSHOT.jar"""; IconFilename: "{app}\jre-21.0.7\bin\javaw.exe"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Criar atalho na Área de Trabalho"; GroupDescription: "Atalhos:"

[Run]
; Descompactar JRE
Filename: "{tmp}\7za.exe"; Parameters: "x ""{tmp}\bellsoft-jre21.0.7+9-windows-amd64.zip"" -o""{app}"" -y"; Flags: runhidden waituntilterminated

; Descompactar MySQL
Filename: "{tmp}\7za.exe"; Parameters: "x ""{tmp}\mysql-8.0.28-winx64.zip"" -o""{app}\mysql"" -y"; Flags: runhidden waituntilterminated

; Executar script de instalação do MySQL
Filename: "{app}\instalador_mysql_3310.bat"; Flags: runhidden waituntilterminated

; Executar sistema após instalação
Filename: "{app}\jre-21.0.7\bin\javaw.exe"; Parameters: "-jar ""{app}\contabilidade-1.0-SNAPSHOT.jar"""; Flags: nowait postinstall skipifsilent
