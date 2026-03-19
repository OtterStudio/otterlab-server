# AWS & CI/CD Rules

- 프로필 분리: 환경 설정은 반드시 `application-local.yml` (H2 또는 로컬 PostgreSQL)과 `application-prod.yml` (AWS RDS)로 분리해서 작성할 것.
- 비밀번호 및 키 관리: `.yml` 파일 내에 DB 비밀번호, JWT 시크릿 키 등을 평문으로 하드코딩하지 말 것. 로컬 환경에서는 환경변수(`${DB_PASSWORD}`)를 사용하고, 운영 환경 설정은 배포 파이프라인에서 주입받을 것.
- CI/CD 워크플로우: GitHub Actions 스크립트는 `.github/workflows/` 디렉토리에 위치시킬 것. 스크립트 작성 시 민감한 값은 반드시 `${{ secrets.SECRET_NAME }}` 문법을 사용하여 GitHub Secrets에서 참조하도록 작성할 것.