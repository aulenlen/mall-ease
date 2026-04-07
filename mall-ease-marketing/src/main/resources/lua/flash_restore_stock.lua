-- KEYS[1] = flash:stock:session:{sessionId}:sku:{skuId}
-- KEYS[2] = flash:bought:session:{sessionId}:sku:{skuId}:uid:{userId}
-- KEYS[3] = flash:limit:session:{sessionId}:sku:{skuId}
-- ARGV[1] = quantity
local quantity = tonumber(ARGV[1]) or 0
redis.call('INCRBY', KEYS[1], quantity)

local flashLimit = tonumber(redis.call('GET', KEYS[3]) or '0') or 0
if flashLimit > 0 then
    local bought = tonumber(redis.call('GET', KEYS[2]) or '0')
    local quantity = tonumber(ARGV[1])
    local remain = bought - quantity
    if remain <= 0 then
        redis.call('DEL', KEYS[2])
    else
        redis.call('DECRBY', KEYS[2], quantity)
    end
end

return 1
