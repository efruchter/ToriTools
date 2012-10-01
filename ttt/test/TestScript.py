from ttt.organization import TTT_EntityScript

class TestScript(TTT_EntityScript):

    def onSpawn(self, entity, scene):
        print "onSpawn() of " + entity.variables.getString("id")
    
    def onUpdate(self, delta, entity, scene):
        print "onUpdate()"

    def onDestroy(self, entity, scene, isRoomEnd):
        print "onDestroy()"
