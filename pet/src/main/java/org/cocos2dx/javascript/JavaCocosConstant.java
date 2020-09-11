package org.cocos2dx.javascript;

/**
 * Created by yuguotao at 2020/9/10,10:11 PM
 */
public interface JavaCocosConstant {
    /** 开启录音*/
    String START_RECORD = "start_record";

    /** 停止录音*/
    String STOP_RECORD = "stop_record";

    /** 做动作，参数：{"action":"cry", "action_type":"normal/pause/resume", "pause_time":500} normal:正常播放一次， pause：播放到pause_time后暂停，resume:恢复动画*/
    String DO_ACTION = "do_action";

}
