package com.example.common.distributedid.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * twitter的snowflake算法生成分布式ID
 * 
 * ID格式：协议格式： 0 - 41位时间戳 - 4位数据中心标识 - 6位机器标识 - 12位序列号
 * 
 * @author zuozhengfeng
 *
 */
public final class SnowFlakeIdGenerator {
	
	private Logger logger = LoggerFactory.getLogger( getClass() );
	
	/**
     * 起始的时间戳，可以修改为服务第一次启动的时间
     * 一旦服务已经开始使用，起始时间戳就不应该改变
     */
    private final static long START_TIMESTAMP 		= 1484754361114L;

    // 每一部分占用的位数
    
    /** 数据中心占用的位数 */
    private final static long DATACENTER_BIT 		= 4;
    
    /** 机器标识占用的位数 */
    private final static long MACHINE_BIT 			= 6;   
    
    /** 序列号占用的位数 */
    private final static long SEQUENCE_BIT 			= 12; 

    // 每一部分的最大值
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    // 每一部分向左的位移
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    
    /** 数据中心 */
    private long datacenterId;  
    
    /** 机器标识 */
    private long machineId;     
    
    /** 序列号 */
    private long sequence = 0L; 
    
    /** 上一次时间戳 */
    private long lastTimestamp = -1L;
    
    /**
     * @param datacenterId  must in [0,MAX_DATACENTER_NUM]
     * @param machineId must in [0,MAX_MACHINE_NUM]
     */
    public SnowFlakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
        
        if ( logger.isInfoEnabled() ) {
        	logger.info( "DistributedId use SnowFlake, datacenterId={}, machineId={}", datacenterId, machineId );
        }
    }
    
    /** 
     * 根据给定的统一的序号ID来获取下一个ID 
     */
    public synchronized long nextId( long unifiedSequenceId ) {
        long currentTimestamp = getNextTimestamp();

        return (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT 
                | datacenterId << DATACENTER_LEFT       
                | machineId << MACHINE_LEFT             
                | ( unifiedSequenceId & MAX_SEQUENCE ); 
    }
    
    /**
     * 获取下一个ID
     * @return
     */
    public synchronized long nextId() {
        long currentTimestamp = getNextTimestamp();
        
        // 拒绝调整当前时钟，否则可能导致重复
        if (currentTimestamp < this.lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currentTimestamp == this.lastTimestamp ) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currentTimestamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L; 
        }

        this.lastTimestamp = currentTimestamp;

        return (currentTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT 
                | datacenterId << DATACENTER_LEFT       
                | machineId << MACHINE_LEFT             
                | sequence;                             
    }

    private long getNextMill() {
        long mill = getNextTimestamp();
        while (mill <= this.lastTimestamp) {
            mill = getNextTimestamp();
        }
        return mill;
    }

    private long getNextTimestamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlakeIdGenerator snowFlake = new SnowFlakeIdGenerator(2, 3);
        long start = System.currentTimeMillis();
        for (int i = 0; i < (1 << 10); i++) {
            System.out.println(i+": "+snowFlake.nextId());
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
