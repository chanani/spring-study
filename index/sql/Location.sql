-- 0. 확인 (100000이어야 함)
SELECT COUNT(*) FROM location;


-- 1. location_point 재구성
DROP TABLE IF EXISTS location_point;

CREATE TABLE location_point
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NULL,
    location POINT        NOT NULL SRID 4326
);

-- POINT(위도 경도) 순서. axis-order 옵션 없음 = MySQL 기본 순서
INSERT INTO location_point (id, name, location)
SELECT id,
       name,
       ST_GeomFromText(CONCAT('POINT(', lat, ' ', lng, ')'), 4326)
FROM location;

CREATE SPATIAL INDEX idx_location ON location_point (location);


-- 2. location 복합 인덱스 (이미 있으면 건너뛰기)
-- CREATE INDEX idx_lat_lng ON location (lat, lng);


-- 3. 통계 갱신
ANALYZE TABLE location;
ANALYZE TABLE location_point;

--

-- 건수
SELECT (SELECT COUNT(*) FROM location)       AS loc,
       (SELECT COUNT(*) FROM location_point) AS pt;
-- 기대: 둘 다 100000

-- 축 순서 (ST_X가 위도)
SELECT id, ST_X(location) AS lat_37, ST_Y(location) AS lng_127
FROM location_point LIMIT 3;
-- 기대: lat_37 = 37.x / lng_127 = 127.x

-- BBox 건수 일치
SELECT COUNT(*) FROM location
WHERE lat BETWEEN 37.4900 AND 37.5100
  AND lng BETWEEN 127.0200 AND 127.0500;

SELECT COUNT(*) FROM location_point
WHERE MBRContains(
              ST_GeomFromText('POLYGON((37.49 127.02,37.49 127.05,37.51 127.05,37.51 127.02,37.49 127.02))', 4326),
              location);
-- 기대: 둘 다 7123
