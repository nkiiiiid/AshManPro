package net.noyark.DreamServerGroup.triggers;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.scheduler.PluginTask;
import net.noyark.DreamServerGroup.AshManPro;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author zzz1999 @ AshManPro Project
 */
public class T2Trigger extends PluginTask<AshManPro> implements BaseTrigger {
    private int entityThreshold;
    private int AvgTPSThreshold;

    public T2Trigger(AshManPro owner, int entityThreshold, int AvgTPSThreshold) {
        super(owner);
        this.entityThreshold = entityThreshold;
        this.AvgTPSThreshold = AvgTPSThreshold;
    }

    @Override
    public void onRun(int i) {
        if (AshManPro.eliminate_entity || AshManPro.eliminate_items) {
            Stream.Builder<Entity> build = Stream.builder();
            owner.getServer().getLevels().values().forEach(l -> Stream.of(l.getEntities()).forEach(build::add));
            Stream<Entity> stream;
            if ((stream = build.build()).count() > entityThreshold && Server.getInstance().getTicksPerSecondAverage() < AvgTPSThreshold) {
                if (AshManPro.beforehand_message.isEmpty()) {
                    AshManPro.getActuator().perform(stream);
                    return;
                }
                Map<Integer, String> bm = AshManPro.beforehand_message;
                owner.getServer().getScheduler().scheduleRepeatingTask(new PluginTask<AshManPro>(AshManPro.getInstance()) {
                    Map<Integer, String> bm = AshManPro.beforehand_message;
                    private int cel = bm.keySet().iterator().next();

                    @Override
                    public void onRun(int i) {
                        if (cel < 0) {
                            AshManPro.getActuator().perform(stream);
                            cancel();
                        }
                        String s;
                        if ((s = bm.get(cel)) != null) {
                            Server.getInstance().broadcastMessage(s, Server.getInstance().getOnlinePlayers().values());
                        }
                        cel--;

                    }
                }, 20);
            }
        }
    }
}
