/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eudemon.flow.util;

import com.eudemon.flow.bpmn.model.BpmnModel;
import com.eudemon.flow.cmmn.image.CaseDiagramGenerator;
import com.eudemon.flow.cmmn.image.impl.DefaultCaseDiagramGenerator;
import com.eudemon.flow.cmmn.model.CmmnModel;
import com.eudemon.flow.image.ProcessDiagramGenerator;
import com.eudemon.flow.image.exception.FlowableImageException;
import com.eudemon.flow.image.impl.DefaultProcessDiagramGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageGenerator {

    public static BufferedImage createImage(BpmnModel bpmnModel) {
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BufferedImage diagramImage = diagramGenerator.generatePngImage(bpmnModel, 1.0);
        return diagramImage;
    }

    public static BufferedImage createImage(BpmnModel bpmnModel, double scaleFactor) {
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator(scaleFactor);
        BufferedImage diagramImage = diagramGenerator.generatePngImage(bpmnModel, scaleFactor);
        return diagramImage;
    }
    
    public static BufferedImage createCmmnImage(CmmnModel cmmnModel, double scaleFactor) {
        CaseDiagramGenerator diagramGenerator = new DefaultCaseDiagramGenerator(scaleFactor);
        BufferedImage diagramImage = diagramGenerator.generatePngImage(cmmnModel, scaleFactor);
        return diagramImage;
    }

    public static byte[] createByteArrayForImage(BufferedImage image, String imageType) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, imageType, out);

        } catch (IOException e) {
            throw new FlowableImageException("Error while generating byte array for process image", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignore) {
                // Exception is silently ignored
            }
        }
        return out.toByteArray();
    }
}
