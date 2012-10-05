from ttt.organization import TTT_EntityScript

class TestScript(TTT_EntityScript):

    def onSpawn(self, entity, scene):
        print "onSpawn() of " + entity.variables.load("id")
	entity.variables.store("d", 30)
	print "\nPosition=" + entity.getPos().toString()
    
    def onUpdate(self, entity, scene, delta):
        print "onUpdate() of " + entity.variables.load("id") + ", with a delta of " + str(delta)
	f = entity.variables.load("d")
	f = f + 4
	entity.variables.store("d", f)

    def onDeath(self, entity, scene, isRoomEnd):
        print "onDestroy() of " + entity.variables.load("id") + "\nvalue of d (34): " + str(entity.variables.load("d"))
