package ar.pablitar.tentacle3d

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.assets.loaders.ModelLoader
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.utils.UBJsonReader
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.AnimationController
import com.badlogic.gdx.Input

class Tentacle3DApp extends ApplicationAdapter {

  lazy val batch = new ModelBatch
  lazy val model = loadModel()
  lazy val modelInstance = new ModelInstance(model)
  lazy val camera = createCamera()
  lazy val environment = createEnvironment()
  lazy val cameraController = createCameraController()
  lazy val animationController = createAnimationController()

  def loadModel(): Model = {
    lazy val modelLoader = new G3dModelLoader(new UBJsonReader)
    modelLoader.loadModel(Gdx.files.getFileHandle("Tentacle3D.g3db", FileType.Internal))
  }

  override def render() = {
    camera.update()
    cameraController.update()
    animationController.update(Gdx.graphics.getDeltaTime)
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

    batch.begin(camera)
    batch.render(modelInstance, environment)
    batch.end()
    
    if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
      animationController.animate("Armature|Jump", null, 0.2f)
      animationController.queue("Armature|Idle", -1, 1.0f, null, 0.2f)
    }

  }

  override def dispose() = {
    batch.dispose()
    model.dispose()
  }

  def createCamera() = {
    val camera = new PerspectiveCamera(67, 1024, 768)
    camera.position.set(10f, 10f, 10f);
    camera.lookAt(0, 0, 0);
    camera.near = 1f;
    camera.far = 300f;
    camera
  }

  def createEnvironment() = {
    val environment = new Environment()
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
    environment
  }

  def createCameraController() = {
    val controller = new CameraInputController(camera)
    Gdx.input.setInputProcessor(controller)
    controller
  }

  def createAnimationController() = {
    val controller = new AnimationController(modelInstance)
    controller.setAnimation("Armature|Idle", -1)
    controller
  }
}