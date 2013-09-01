package script;


public class EventTest
{
//	@BeforeClass
//	public static void setUp()
//	{
//		TestStartupManager.setupTests(TestStartupManager.DATABASE + TestStartupManager.PLUGINS + TestStartupManager.SCRIPTS);
//	}
//
//	@AfterClass
//	public static void tearDown()
//	{
//		ScriptExecuter.shutdown();
//	}
//	
//	@After
//	public void postTest()
//	{	// beende Szene und Session, falls forhanden
//		Game.endGame();
//		Game.newScene(null);
//	}
//	
//	@Test
//	public void test()
//	{
//		ScriptExecuter.eval(
//				"class Event_001 < Java::CuinaJunitScript::TaskWrapper\n" +
//				"	def initialize\n" +	
//				"		super\n" +
//				"	end\n" +
//				"	def start\n" +
//				"		puts 'A'\n" +
//				"		Java::CuinaJunitScript::EventTest.waitFrames\n" +
//				"		puts 'B'\n" +
//				"	end\n" +
//				"end");
//		
//		IRubyObject c1 = ScriptExecuter.eval("Event_001.new");
//		IRubyObject c2 = ScriptExecuter.eval("Event_001.new");
//		
//		CoroutineTask task1 = new CoroutineTask((TaskWrapper) c1.toJava(TaskWrapper.class));
//		CoroutineTask task2 = new CoroutineTask((TaskWrapper) c2.toJava(TaskWrapper.class));
//		task1.run();
//		task2.run();
//		
//		task1.run();
//		task2.run();
//	}
//	
//	// Libary-Simulation
//	public static void waitFrames() throws SuspendExecution
//	{
//		CoroutineTask.yield();
//	}
}
