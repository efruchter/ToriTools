package toritools.timing;

public class RepeatingTimer {

	private RepeatingTimerAction task;

	private boolean running;

	private long delay;

	public RepeatingTimer(final RepeatingTimerAction task,
			final long MILLI_DELAY) {
		this.task = task;
		delay = MILLI_DELAY;
	}

	public void start() {

		running = true;

		new Thread() {
			private long lastFrame;
			private long lastFrameDelta = delay;

			@Override
			public void run() {
				while (RepeatingTimer.this.running) {
					lastFrame = System.currentTimeMillis();
					RepeatingTimer.this.task.update(lastFrameDelta);
					lastFrameDelta = lastFrame + delay
							- System.currentTimeMillis();
					if (lastFrameDelta > 0) {
						try {
							Thread.sleep(lastFrameDelta);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	public void stop() {
		running = false;
	}

	public void setDelay(final long delay) {
		this.delay = delay;
	}

	public void setTask(final RepeatingTimerAction task) {
		this.task = task;
	}

	public static interface RepeatingTimerAction {
		void update(long lastFrameDelta);
	}
}
