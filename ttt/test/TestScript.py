from ttt.organization import TTT_EntityScript

class TestScript(TTT_EntityScript):

    def onSpawn(self, entity, scene):
        print "onSpawn() of " + entity.variables.getString("id")
    
    def onUpdate(self, entity, scene, delta):
        print "onUpdate() of " + entity.variables.getString("id") + ", with a delta of " + str(delta)

    def onDeath(self, entity, scene, isRoomEnd):
        print "onDestroy() of " + entity.variables.getString("id")
