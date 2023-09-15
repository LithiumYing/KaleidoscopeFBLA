@echo off
python -m black ./backend
echo.
if "%~1"=="--rejoin" (
    git subtree split --prefix backend -b deploy --rejoin
) else (
    git subtree split --prefix backend -b deploy
)
echo.
git push origin deploy
echo.
git push origin main
