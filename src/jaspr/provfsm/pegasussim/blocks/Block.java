//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim.blocks;

import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.pegasussim.PegasusWorkflow;
import java.util.List;
import java.util.Set;

public abstract class Block {
    public static final int MAX_SIZE = 100;
    private int numInputs;
    private int numOutputs;
    List<ITask> tasks;

    public Block getNext(Block.BlockType typ, int size, List<ITask> allTasks) {
        if (typ == Block.BlockType.Pipline) {
            return new ProcessBlock(this.numOutputs(), allTasks);
        } else if (typ == Block.BlockType.Distribution) {
            return new DistributionBlock(this.numOutputs(), size * this.numOutputs < 100 ? size * this.numOutputs() : 100, allTasks);
        } else if (typ == Block.BlockType.Aggregation) {
            return new AggregationBlock(this.numOutputs(), size < this.numOutputs() ? size : this.numOutputs(), allTasks);
        } else {
            return typ == Block.BlockType.Redistribution ? new RedistributionBlock(this.numOutputs(), this.numOutputs(), size, allTasks) : null;
        }
    }

    public static Block init(Block.BlockType typ, int size, List<ITask> allTasks) {
        if (typ == Block.BlockType.Pipline) {
            return new ProcessBlock(1, allTasks);
        } else if (typ == Block.BlockType.Distribution) {
            return new DistributionBlock(1, size, allTasks);
        } else if (typ == Block.BlockType.Aggregation) {
            return new AggregationBlock(size, 1, allTasks);
        } else {
            return typ == Block.BlockType.Redistribution ? new RedistributionBlock(size, size, 1, allTasks) : null;
        }
    }

    public Block(int numInputs, int numOutputs, int numTasks, List<ITask> allTasks) {
        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        this.tasks = RandUtil.samplewr(allTasks, numTasks);
    }

    public int numInputs() {
        return this.numInputs;
    }

    public int numOutputs() {
        return this.numOutputs;
    }

    public int numTasks() {
        return this.tasks.size();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "\t" + this.numInputs() + "x" + this.numTasks() + "->" + this.numOutputs();
    }

    public abstract List<IWidget> doTasks(PegasusWorkflow var1, List<IWidget> var2, List<IWorker> var3, Set<IWorker> var4);

    public static enum BlockType {
        Pipline,
        Distribution,
        Aggregation,
        Redistribution,
        ReturnBlock;

        private BlockType() {
        }
    }
}
