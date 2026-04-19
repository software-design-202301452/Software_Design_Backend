#!/bin/bash
# CEW-76: MySQL 데이터베이스 백업 스크립트
# 환경변수에서 DB 연결 정보를 읽어 mysqldump 실행
# 백업 파일은 30일 후 자동 삭제

set -euo pipefail

# ─── 설정 ────────────────────────────────────────────────────────────────────
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-software_design}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"

BACKUP_DIR="${BACKUP_DIR:-/var/backups/software_design}"
RETENTION_DAYS="${RETENTION_DAYS:-30}"

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/backup_${DB_NAME}_${TIMESTAMP}.sql.gz"

# ─── 백업 디렉토리 생성 ───────────────────────────────────────────────────────
mkdir -p "${BACKUP_DIR}"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 백업 시작 - DB: ${DB_NAME}, Host: ${DB_HOST}:${DB_PORT}"

# ─── mysqldump 실행 및 gzip 압축 ─────────────────────────────────────────────
if mysqldump \
    --host="${DB_HOST}" \
    --port="${DB_PORT}" \
    --user="${DB_USER}" \
    --password="${DB_PASSWORD}" \
    --single-transaction \
    --quick \
    --lock-tables=false \
    "${DB_NAME}" | gzip > "${BACKUP_FILE}"; then

    FILE_SIZE=$(du -sh "${BACKUP_FILE}" | cut -f1)
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 백업 완료 - 파일: ${BACKUP_FILE} (${FILE_SIZE})"
else
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 백업 실패" >&2
    exit 1
fi

# ─── 30일 이상 된 백업 파일 삭제 ─────────────────────────────────────────────
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 오래된 백업 파일 정리 (${RETENTION_DAYS}일 초과)"
find "${BACKUP_DIR}" -name "backup_${DB_NAME}_*.sql.gz" -mtime "+${RETENTION_DAYS}" -delete
REMAINING=$(find "${BACKUP_DIR}" -name "backup_${DB_NAME}_*.sql.gz" | wc -l | tr -d ' ')
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 남은 백업 파일 수: ${REMAINING}"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] 백업 정책 완료"
