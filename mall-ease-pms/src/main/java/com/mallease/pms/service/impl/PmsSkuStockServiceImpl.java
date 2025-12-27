package com.mallease.pms.service.impl;

import com.mallease.common.constant.PmsRedisKeys;
import com.mallease.common.service.RedisService;
import com.mallease.pms.dao.PmsSkuStockDao;
import com.mallease.pms.pojo.PmsSkuStock;
import com.mallease.pms.service.PmsSkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * SKU库存服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@Slf4j
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockDao skuStockDao;
    @Autowired
    private RedisService redisService;

    @Override
    public Integer createBatch(List<PmsSkuStock> stockList) {
        return skuStockDao.insertBatch(stockList);
    }

    @Override
    public boolean deductStock(Long spuId, Long skuId, Integer quantity) {
        String stockKey = PmsRedisKeys.SPU_SKU_STOCK_PREFIX + spuId;
        String skuField = String.valueOf(skuId);

        // Lua 脚本保证原子性（适配 Hash 结构）
        String luaScript =
                "local stock = redis.call('HGET', KEYS[1], ARGV[1]) " +
                        "if stock and tonumber(stock) >= tonumber(ARGV[2]) then " +
                        "  redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2]) " +
                        "  return 1 " +
                        "else " +
                        "  return 0 " +
                        "end";

        // 执行 Lua 脚本
        Long result = (Long) redisService.execute(
                luaScript,
                Collections.singletonList(stockKey),
                List.of(skuField, quantity.toString())
        );

        if (result == 1) {
            log.info("扣减SKU库存成功，spuId={}, skuId={}, quantity={}",
                    spuId, skuId, quantity);

            // 检查是否售罄
            Object remainStockObj = redisService.hGet(stockKey, skuField);
            if (remainStockObj != null && Integer.parseInt(remainStockObj.toString()) == 0) {
                log.warn("SKU已售罄，spuId={}, skuId={}", spuId, skuId);
                // TODO: 通知ES更新商品库存状态（如果所有SKU都售罄）
            }

            return true;
        }

        log.warn("SKU库存不足，spuId={}, skuId={}, 尝试扣减={}",
                spuId, skuId, quantity);
        return false;
    }

    @Override
    public List<PmsSkuStock> listStockBySpuIds(List<Long> spuList) {
        return skuStockDao.selectBySpuIds(spuList);
    }
//    @Scheduled(cron = "0 */5 * * * ?")  // 每5分钟
//    public void syncStockToDatabase() {
//        // 从Redis读取库存变化，批量更新数据库
//        // 避免每次扣减都写数据库，减轻DB压力
//    }

}

