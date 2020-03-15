import org.deeplearning4j.nn.modelimport.keras.KerasModelImport
import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.factory.Nd4j
import java.io.File


fun main() {
    val file = File("assets/model.h5")
    val model = KerasModelImport.importKerasSequentialModelAndWeights(file.absolutePath)


    val input = Nd4j.create(DataType.FLOAT, 216, 1, 1)
    val output = model.output(input)

    model.fit(input, output)

}