# WorkStealingThreadPool
In a work stealing scheduler, each processor in the computer system has a queue of work tasks to perform. while
running, each task can spawn a new task or more that can feasibly be executed in parallel with its other work.
When a processor runs out of work, it looks at the queues of other processors and steals their work items.
Each processor is a thread which maintains local work queue. A processor can push and pop tasks from its local
queue. Also, a processor can pop tasks from other processor’s queue by the steal action.
