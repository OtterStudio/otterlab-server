-- 0. UUID 생성을 위한 확장 모듈 활성화 (권장)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. 사용자 테이블 (소셜 로그인 및 상태 관리)
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     social_id VARCHAR(255) UNIQUE NOT NULL,
                                     provider VARCHAR(20) NOT NULL,
                                     nickname VARCHAR(50) NOT NULL,
                                     age_group INT,
                                     gender VARCHAR(10),
                                     comment_blocked_until TIMESTAMP,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 밸런스 게임 문항 테이블
-- 만약 'game_status'가 이미 존재한다는 에러가 나면 아래 DROP 구문을 먼저 실행하세요.
-- DROP TYPE IF EXISTS game_status;
DO $$ BEGIN
    CREATE TYPE game_status AS ENUM ('PENDING', 'ACTIVE', 'REJECTED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS games (
                                     id SERIAL PRIMARY KEY,
                                     creator_id INT REFERENCES users(id),
                                     title VARCHAR(255) NOT NULL,
                                     option_a_text TEXT NOT NULL,
                                     option_b_text TEXT NOT NULL,
                                     category VARCHAR(50),
                                     status game_status DEFAULT 'PENDING',
                                     view_count INT DEFAULT 0,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 투표 기록 (회원 전용)
CREATE TABLE IF NOT EXISTS votes (
                                     id SERIAL PRIMARY KEY,
                                     user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     game_id INT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                                     selected_option CHAR(1) CHECK (selected_option IN ('A', 'B')),
                                     voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     UNIQUE(user_id, game_id)
);

-- 4. 댓글 (진영 표시 및 랜덤 닉네임)
CREATE TABLE IF NOT EXISTS comments (
                                        id SERIAL PRIMARY KEY,
                                        user_id INT REFERENCES users(id) ON DELETE SET NULL,
                                        game_id INT REFERENCES games(id) ON DELETE CASCADE,
                                        content TEXT NOT NULL,
                                        side CHAR(1) NOT NULL CHECK (side IN ('A', 'B')),
                                        report_count INT DEFAULT 0,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 댓글 신고 로그
CREATE TABLE IF NOT EXISTS comment_reports (
                                               id SERIAL PRIMARY KEY,
                                               comment_id INT REFERENCES comments(id) ON DELETE CASCADE,
                                               reporter_id INT REFERENCES users(id),
                                               reason TEXT,
                                               reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. 궁합 테스트 (UUID 기반 비회원 공유)
CREATE TABLE IF NOT EXISTS compatibility_tests (
                                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   creator_id INT REFERENCES users(id),
                                                   title VARCHAR(255),
                                                   question_ids INT[] NOT NULL,
                                                   creator_answers CHAR(1)[] NOT NULL,
                                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 성능 최적화용 인덱스
CREATE INDEX IF NOT EXISTS idx_game_category_status ON games(category, status);
CREATE INDEX IF NOT EXISTS idx_comment_game_id ON comments(game_id);