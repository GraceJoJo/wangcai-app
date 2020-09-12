package org.cocos2dx.javascript;

/**
 * Created by yuguotao at 2020/9/10,10:11 PM
 */
public interface JavaCocosConstant {
    /** 开启录音*/
    String TYPE_START_RECORD = "start_record";

    /** 停止录音*/
    String TYPE_STOP_RECORD = "stop_record";

    /** 做动作，参数：{"action":"cry", "action_type":"normal/pause/resume", "pause_time":500} normal:正常播放一次， pause：播放到pause_time后暂停，resume:恢复动画*/
    String TYPE_DO_ACTION = "do_action";



    /** 动作：哭*/
    String ACTION_CRY = "cry";
    /** 动作：笑*/
    String ACTION_HAPPY = "happy";
    /** 动作：跳*/
    String ACTION_JUMP = "jump";
    /** 动作：打招呼*/
    String ACTION_GREET = "greet";
    /** 动作：听*/
    String ACTION_LISTEN = "listen";

}
