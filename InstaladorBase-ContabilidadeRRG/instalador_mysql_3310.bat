@echo off
setlocal enabledelayedexpansion

:: === EVITAR DUPLA EXECUÇÃO ===
set "LOCKFILE=%~dp0bat_executado.lock"
if exist "%LOCKFILE%" (
    echo Script já foi executado. Abortando.
    exit /b 0
)
echo Executando script pela primeira vez... > "%LOCKFILE%"

:: === CONFIGURAÇÕES INICIAIS ===
set "MYSQL_DIR=%~dp0mysql\mysql-8.0.28-winx64"
set "MYSQL_BIN=%MYSQL_DIR%\bin"
set "PORTA_INICIAL=3310"
set "SENHA_ROOT=1998Fisica2025"
set "JAR_DIR=%~dp0"
set "JRE_DIR=%~dp0jre-21.0.7"
set "PORTA_SELECIONADA="

:: === ENCONTRAR PORTA LIVRE ===
set PORTA=%PORTA_INICIAL%
:verifica_porta
netstat -ano | findstr ":%PORTA%" >nul
if %errorlevel%==0 (
    set /a PORTA+=1
    goto verifica_porta
)
set "PORTA_SELECIONADA=%PORTA%"
echo Porta %PORTA_SELECIONADA% será usada para o MySQL

:: === INICIALIZAR MYSQL ===
cd /d "%MYSQL_BIN%"
echo Inicializando MySQL...

"%MYSQL_BIN%\mysqld.exe" --initialize-insecure --basedir="%MYSQL_DIR%" --datadir="%MYSQL_DIR%\data" --explicit_defaults_for_timestamp

:: === CRIAR ARQUIVO my.ini DINÂMICO ===
(
  echo [mysqld]
  echo port=%PORTA_SELECIONADA%
  echo basedir=%MYSQL_DIR%
  echo datadir=%MYSQL_DIR%\data
  echo explicit_defaults_for_timestamp=1
) > "%MYSQL_DIR%\my.ini"

:: === INSTALAR SERVIÇO DO MYSQL ===
"%MYSQL_BIN%\mysqld.exe" --install MySQLRRG3310 --defaults-file="%MYSQL_DIR%\my.ini"
net start MySQLRRG3310

:: === CRIAR USUÁRIO ROOT COM SENHA ===
timeout /t 10 >nul
"%MYSQL_BIN%\mysql.exe" -u root --port=%PORTA_SELECIONADA% -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '%SENHA_ROOT%'; FLUSH PRIVILEGES;"

echo MySQL instalado e configurado com sucesso.
exit /b 0
