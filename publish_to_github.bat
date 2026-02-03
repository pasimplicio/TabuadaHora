@echo off
cls

REM ================================
REM CONFIGURAÇÕES (EDITE AQUI)
REM ================================
SET PROJECT_DIR=C:\Users\caema43907\FourOperations
SET GITHUB_REPO_URL=https://github.com/pasimplicio/TabuadaHora.git
SET BRANCH=main

echo ================================
echo Subindo projeto Android para GitHub
echo ================================

cd /d %PROJECT_DIR%

REM ================================
REM INICIALIZA GIT
REM ================================
if not exist ".git" (
    echo Inicializando repositório Git...
    git init
) else (
    echo Repositório Git já existe.
)

REM ================================
REM CRIA .gitignore ANDROID CORRETO
REM ================================
if not exist ".gitignore" (
    echo Criando .gitignore...
    (
        echo # Android
        echo *.iml
        echo .gradle
        echo /local.properties
        echo /.idea/caches
        echo /.idea/libraries
        echo /.idea/modules.xml
        echo /.idea/workspace.xml
        echo /.idea/navEditor.xml
        echo /.idea/assetWizardSettings.xml
        echo /.idea/deploymentTargetSelector.xml
        echo .DS_Store
        echo /build
        echo /captures
        echo .externalNativeBuild
        echo .cxx
        echo *.apk
        echo *.aab
    ) > .gitignore
) else (
    echo .gitignore já existe.
)

REM ================================
REM ADICIONA ARQUIVOS
REM ================================
echo Adicionando arquivos ao Git...
git add .

REM ================================
REM COMMIT
REM ================================
echo Criando commit...
git commit -m "Primeira versão do app TabuadaHora"

REM ================================
REM CONFIGURA BRANCH
REM ================================
git branch -M %BRANCH%

REM ================================
REM CONFIGURA REMOTE
REM ================================
git remote remove origin >nul 2>&1
git remote add origin %GITHUB_REPO_URL%

REM ================================
REM PUSH
REM ================================
echo Enviando para o GitHub...
git push -u origin %BRANCH%

echo ================================
echo PROJETO PUBLICADO COM SUCESSO!
echo ================================
pause
