-- 1. 사용자 테이블 (소셜 로그인 및 상태 관리)
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       social_id VARCHAR(255) UNIQUE NOT NULL, -- 구글/카카오 고유 식별값
                       provider VARCHAR(20) NOT NULL, -- 'GOOGLE', 'KAKAO'
                       nickname VARCHAR(50) NOT NULL, -- 랜덤 닉네임 또는 유저 설정
                       age_group INT,
                       gender VARCHAR(10),
                       comment_blocked_until TIMESTAMP, -- 신고로 인한 정지 기한
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 밸런스 게임 문항 테이블
CREATE TYPE game_status AS ENUM ('PENDING', 'ACTIVE', 'REJECTED');

CREATE TABLE games (
                       id SERIAL PRIMARY KEY,
                       creator_id INT REFERENCES users(id),
                       title VARCHAR(255) NOT NULL,
                       option_a_text TEXT NOT NULL,
                       option_b_text TEXT NOT NULL,
                       category VARCHAR(50), -- 연애, 음식, 황당, 윤리 등
                       status game_status DEFAULT 'PENDING',
                       view_count INT DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 투표 기록 (회원 전용)
CREATE TABLE votes (
                       id SERIAL PRIMARY KEY,
                       user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       game_id INT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                       selected_option CHAR(1) CHECK (selected_option IN ('A', 'B')),
                       voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       UNIQUE(user_id, game_id) -- 중복 투표 방지
);

-- 4. 댓글 (진영 표시 및 랜덤 닉네임)
CREATE TABLE comments (
                          id SERIAL PRIMARY KEY,
                          user_id INT REFERENCES users(id) ON DELETE SET NULL,
                          game_id INT REFERENCES games(id) ON DELETE CASCADE,
                          content TEXT NOT NULL,
                          side CHAR(1) NOT NULL CHECK (side IN ('A', 'B')), -- 투표 진영 표시
                          report_count INT DEFAULT 0, -- 누적 신고 수
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 댓글 신고 로그
CREATE TABLE comment_reports (
                                 id SERIAL PRIMARY KEY,
                                 comment_id INT REFERENCES comments(id) ON DELETE CASCADE,
                                 reporter_id INT REFERENCES users(id),
                                 reason TEXT,
                                 reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. 궁합 테스트 (UUID 기반 비회원 공유)
CREATE TABLE compatibility_tests (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- 고유 링크용
                                     creator_id INT REFERENCES users(id),
                                     title VARCHAR(255),
                                     question_ids INT[] NOT NULL, -- 10문제 ID 리스트
                                     creator_answers CHAR(1)[] NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 성능 최적화용 인덱스
CREATE INDEX idx_game_category_status ON games(category, status);
CREATE INDEX idx_comment_game_id ON comments(game_id);