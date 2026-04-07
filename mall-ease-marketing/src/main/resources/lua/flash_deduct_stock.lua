-- KEYS[1] = flash:stock:session:{sessionId}:sku:{skuId}
-- KEYS[2] = flash:bought:session:{sessionId}:sku:{skuId}:uid:{userId}
-- KEYS[3] = flash:meta:session:{sessionId}
-- KEYS[4] = flash:limit:session:{sessionId}:sku:{skuId}
--
-- ARGV[1] = quantity
-- ARGV[2] = nowMillis
--
-- return:
--   1  = success
--  -1  = stock_not_enough
--  -2  = limit_exceeded
--  -3  = session_invalid
--  -4  = session_meta_not_found
local meta = redis.call('GET', KEYS[3])
if not meta then
    return -4
end

local metaObj = cjson.decode(meta)
local now = tonumber(ARGV[2])
local startTime = tonumber(metaObj['startTime'])
local endTime = tonumber(metaObj['endTime'])

if not startTime or not endTime then
    return -4
end

if now < startTime then
      return -3
end
if now > endTime then
    return -3
end

local flashLimit = tonumber(redis.call('GET', KEYS[4]) or '0') or 0
local quantity = tonumber(ARGV[1] or '0') or 0

if flashLimit > 0 then
    local bought = tonumber(redis.call('GET', KEYS[2]) or '0') or 0
    if bought + quantity > flashLimit then
        return -2
    end
end

local stock = tonumber(redis.call('GET', KEYS[1]) or '0') or 0
if stock < quantity then
    return -1
end

redis.call('DECRBY', KEYS[1], quantity)

if flashLimit > 0 then
        redis.call('INCRBY', KEYS[2], quantity)
    local ttl = tonumber(redis.call('TTL', KEYS[1]) or '-1') or -1
    if ttl > 0 then
        redis.call('EXPIRE', KEYS[2], ttl)
    end
end

return 1

