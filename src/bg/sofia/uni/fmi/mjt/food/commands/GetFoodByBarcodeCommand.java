package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.exceptions.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GetFoodByBarcodeCommand extends Command {
    private static final String CODE_OPTION = "--code";
    private static final String IMAGE_OPTION = "--img";
    private static final String OPTION_SEPARATOR = "=";
    private static final int ONE_ARG = 1;
    private static final int TWO_ARGS = 2;
    private static final int FIST_ARG = 0;
    private static final int SECOND_ARG = 1;
    protected static final String GET_FOOD_BY_BARCODE_CMD = "get-food-by-barcode";
    protected static final String INVALID_ARG_OPTION_MSG = "invalid argument option";
    protected static final String READING_BARCODE_ERROR_MSG = "problems while reading the barcode image";
    protected static final String FOOD_NOT_FOUND_MSG = "food not found";

    public GetFoodByBarcodeCommand(int minArgsCount, int maxArgsCount, Receiver receiver) {
        super(minArgsCount, maxArgsCount, GET_FOOD_BY_BARCODE_CMD, receiver);
    }

    @Override
    public String execute(List<String> arguments) {
        if (arguments.size() < minArgsCount || arguments.size() > maxArgsCount) {
            return INVALID_ARGS_COUNT_MSG;
        }

        try {
            String argument = processArguments(arguments);
            if (getArgumentOption(argument).equals(CODE_OPTION)) {
                return searchByCode(argument);
            } else {
                return searchByImage(argument);
            }
        } catch (InvalidCommandArgumentException e) {
            return INVALID_ARG_OPTION_MSG;
        } catch (ChecksumException | NotFoundException | IOException | FormatException e) {
            return READING_BARCODE_ERROR_MSG;
        }
    }

    private String processArguments(List<String> arguments) throws InvalidCommandArgumentException {
        if (arguments.size() == TWO_ARGS) {
            if (hasValidOption(arguments.get(FIST_ARG)) && hasValidOption(arguments.get(SECOND_ARG))) {
                String argOption1 = getArgumentOption(arguments.get(FIST_ARG));
                String argOption2 = getArgumentOption(arguments.get(SECOND_ARG));
                if (argOption1.equals(CODE_OPTION) && argOption2.equals(IMAGE_OPTION)) {
                    return arguments.get(FIST_ARG);
                } else if (argOption1.equals(IMAGE_OPTION) && argOption2.equals(CODE_OPTION)) {
                    return arguments.get(SECOND_ARG);
                }
            }
        } else if (arguments.size() == ONE_ARG) {
            if (hasValidOption(arguments.get(FIST_ARG))) {
                return arguments.get(FIST_ARG);
            }
        }

        throw new InvalidCommandArgumentException(INVALID_ARG_OPTION_MSG);
    }

    private String getArgumentOption(String argument) {
        int index = argument.indexOf(OPTION_SEPARATOR);
        return argument.substring(0, index);
    }

    private String removeOption(String argument) {
        int index = argument.indexOf(OPTION_SEPARATOR);
        return argument.substring(index + 1);
    }

    private boolean hasValidOption(String argument) {
        if (!argument.contains(OPTION_SEPARATOR)) {
            return false;
        }
        String option = getArgumentOption(argument);
        return option.equals(CODE_OPTION) || option.equals(IMAGE_OPTION);
    }

    private String searchByCode(String argument) {
        String gtinUpc = removeOption(argument);
        Food food = receiver.getFoodByGtinUpc(gtinUpc);
        return food == null ? FOOD_NOT_FOUND_MSG : food.toString();
    }

    private String searchByImage(String argument)
        throws IOException, ChecksumException, NotFoundException, FormatException {
        String path = removeOption(argument);

        InputStream barCodeInputStream = new FileInputStream(path);
        BufferedImage barCodeBufferedImage = ImageIO.read(barCodeInputStream);
        LuminanceSource source = new BufferedImageLuminanceSource(barCodeBufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Result result = reader.decode(bitmap);

        return searchByCode(result.getText());
    }
}
