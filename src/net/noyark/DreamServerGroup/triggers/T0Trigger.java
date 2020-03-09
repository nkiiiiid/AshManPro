package net.noyark.DreamServerGroup.triggers;

import cn.nukkit.entity.Entity;
import cn.nukkit.scheduler.PluginTask;
import net.noyark.DreamServerGroup.AshManPro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class T0Trigger extends PluginTask<AshManPro> implements BaseTrigger {
    private int threshold;

    public T0Trigger(AshManPro owner, int threshold) {
        super(owner);
        this.threshold = threshold;
    }

    @Override
    public void onRun(int i) {
        if (AshManPro.eliminate_entity || AshManPro.eliminate_items) {
            List<Entity> entityList = new ArrayList<>();
            owner.getServer().getLevels().values().forEach(l -> {
                Stream.of(l.getEntities()).forEach(entityList::add);
            });
            if (entityList.size() > threshold) {
                if (AshManPro.beforehand_message.isEmpty()) {
                    AshManPro.getActuator().perform(entityList);
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
