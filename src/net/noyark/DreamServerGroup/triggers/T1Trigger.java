package net.noyark.DreamServerGroup.triggers;

import cn.nukkit.scheduler.PluginTask;
import net.noyark.DreamServerGroup.AshManPro;

import java.util.Map;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class T1Trigger extends PluginTask<AshManPro> implements BaseTrigger {
    private int threshold;

    public T1Trigger(AshManPro owner, int threshold) {
        super(owner);
        this.threshold = threshold;
    }

    @Override
    public void onRun(int i) {
        if (AshManPro.eliminate_entity || AshManPro.eliminate_items) {
            if (AshManPro.getInstance().getServer().getTicksPerSecondAverage() < threshold) {
                if (AshManPro.beforehand_message.isEmpty()) {
                    AshManPro.getActuator().perform();
                    return;
                }
                owner.getServer().getScheduler().scheduleRepeatingTask(new PluginTask<AshManPro>(AshManPro.getInstance()) {
                    Map<Integer, String> bm = AshManPro.beforehand_message;
                    private int cel = bm.keySet().iterator().next();

                    @Override
                    public void onRun(int i) {
                        if (cel < 0) {
                            AshManPro.getActuator().perform();
                            cancel();
                        }
                        String s;
                        if ((s = bm.get(cel--)) != null) {
                            AshManPro.broadcastMessage(s);
                        }
                    }
                }, 20);
            }
        }
    }
}
