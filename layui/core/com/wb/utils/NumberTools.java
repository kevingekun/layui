package com.wb.utils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.apache.commons.lang.StringUtils;

public final class NumberTools
{
  public static String format(double d, String format)
  {
    DecimalFormat numberFormat = new DecimalFormat(format);

    return numberFormat.format(d);
  }

  public static String format(float f, String format)
  {
    DecimalFormat numberFormat = new DecimalFormat(format);

    return numberFormat.format(f);
  }

  public static String format(int i, String format)
  {
    DecimalFormat numberFormat = new DecimalFormat(format);

    return numberFormat.format(i);
  }

  public static String format(long l, String format)
  {
    DecimalFormat numberFormat = new DecimalFormat(format);

    return numberFormat.format(l);
  }

  public static double add(double[] values)
  {
    double[] arrayOfDouble;
    BigDecimal sum = new BigDecimal("0.0");

    int j = (arrayOfDouble = values).length; for (int i = 0; i < j; ++i) { double v = arrayOfDouble[i];
      sum = sum.add(new BigDecimal(String.valueOf(v)));
    }

    return sum.doubleValue();
  }

  public static double multi(double[] values)
  {
    double[] arrayOfDouble;
    BigDecimal sum = new BigDecimal(0);

    int j = (arrayOfDouble = values).length; for (int i = 0; i < j; ++i) { double v = arrayOfDouble[i];
      sum = sum.multiply(new BigDecimal(v));
    }
    return sum.doubleValue();
  }

  public static double div(double v1, double v2)
  {
    return new BigDecimal(v1).divide(new BigDecimal(v2)).doubleValue();
  }

  public static double sub(double v1, double v2)
  {
    return new BigDecimal(v1).subtract(new BigDecimal(v2)).doubleValue();
  }

  public static double roundup(double value, int pos)
  {
    if (pos < 0)
      throw new RuntimeException("淇濈暀鐨勫皬鏁扮偣(pos)涓嶈兘灏忎簬0!");

    double result = 0D;

    String truncValue = trunc(value, pos);
    String truncValue1 = trunc(value, pos + 1);

    double dValue = Double.valueOf(truncValue).doubleValue();
    double dValue1 = Double.valueOf(truncValue1).doubleValue();

    result = Double.valueOf(truncValue).doubleValue();

    if (dValue < dValue1)
    {
      StringBuffer sb = new StringBuffer();

      if (pos > 0)
        sb.append("0.");

      for (int i = 0; i < pos - 1; ++i)
        sb.append("0");

      sb.append("1");

      double deviation = Double.valueOf(sb.toString()).doubleValue();

      result = add(new double[] { deviation, result });
    }

    return result;
  }

  public static String trunc(double value, int pos)
  {
    StringBuffer format = new StringBuffer("0");

    if (pos >= 0) {
      format.append(".");
      for (int i = 0; i < pos; ++i)
        format.append("0");
    }

    format.append("0");

    String sValue = format(value, format.toString());

    return sValue.substring(0, sValue.length() - 1);
  }

  public static long jianJiaoJinYuan(double value)
  {
    return (long) roundup(value, 0);
  }

  public static double jianFenJinJiao(double value)
  {
    return roundup(value, 1);
  }

  public static double jianLiJinFen(double value)
  {
    return roundup(value, 2);
  }

  public static long round(double d)
  {
    BigDecimal bd = new BigDecimal(String.valueOf(d));

    return bd.longValue();
  }

  public static double round(double d, int scale)
  {
    BigDecimal bd = new BigDecimal(String.valueOf(d)).setScale(scale, 
      4);

    return bd.doubleValue();
  }

  public static boolean isDigit(String str)
  {
    return StringUtils.isNumeric(str);
  }

  public static int mod(int x, int y)
  {
    int z = x / y;
    int m = x - y * z;
    return m;
  }

  public static final BigDecimal sum(boolean round, BigDecimal[] numArray)
  {
    BigDecimal[] arrayOfBigDecimal;
    if (numArray == null)
      return BigDecimal.ZERO;

    BigDecimal result = BigDecimal.ZERO;
    int j = (arrayOfBigDecimal = numArray).length; for (int i = 0; i < j; ++i) { BigDecimal num = arrayOfBigDecimal[i];
      if (num != null)
        result = result.add(num);

    }

    return ((round) ? result.setScale(2, 4) : result);
  }

  public static final BigDecimal sum(BigDecimal[] numArray)
  {
    return sum(false, numArray);
  }

  public static BigDecimal subtract(boolean round, BigDecimal master, BigDecimal[] bigDecimals)
  {
    BigDecimal[] arrayOfBigDecimal;
    BigDecimal result = (master == null) ? BigDecimal.ZERO : master;
    int j = (arrayOfBigDecimal = bigDecimals).length; for (int i = 0; i < j; ++i) { BigDecimal bigDecimal = arrayOfBigDecimal[i];
      if (bigDecimal != null)
        result = result.subtract(bigDecimal);
    }

    return ((round) ? result.setScale(2, 4) : result);
  }

  public static BigDecimal subtract(BigDecimal master, BigDecimal[] bigDecimals)
  {
    return subtract(false, master, bigDecimals);
  }

  public static BigDecimal multiply(boolean round, BigDecimal[] bigDecimals)
  {
    BigDecimal[] arrayOfBigDecimal;
    if ((bigDecimals == null) || (hasZeroOrNull(bigDecimals)))
      return BigDecimal.ZERO;

    BigDecimal result = null;
    int j = (arrayOfBigDecimal = bigDecimals).length; for (int i = 0; i < j; ++i) { BigDecimal bigDecimal = arrayOfBigDecimal[i];
      if (bigDecimal != null)
        if (result == null) {
          result = bigDecimal;
        }
        else
          result = result.multiply(bigDecimal);
    }

    result = (result == null) ? BigDecimal.ZERO : result;
    return ((round) ? result.setScale(2, 4) : result);
  }
  public static BigDecimal multiply(BigDecimal ...bigDecimalss)
  {
    return multiply(false, bigDecimalss);
  }

  public static BigDecimal divide(BigDecimal num1, BigDecimal... num2)
  {
    return divide(num1, false, 2, 4, num2);
  }
  public static BigDecimal divide(BigDecimal num1, int scale, int roundingMode, BigDecimal[] num2)
  {
    return divide(num1, scale, roundingMode, num2);
  }

  public static BigDecimal divide(BigDecimal num1, int roundingMode, BigDecimal[] num2)
  {
    return divide(num1, 2, roundingMode, num2);
  }

  public static BigDecimal divide(BigDecimal num1, boolean throwException, int scale, int roundingMode, BigDecimal[] num2)
  {
    BigDecimal[] arrayOfBigDecimal;
    if (hasZeroOrNull(new BigDecimal[] { num1 }))
      return BigDecimal.ZERO;

    if (hasZeroOrNull(num2)) {
      if (throwException)
        throw new ArithmeticException("闄ゆ暟涓嶅悎娉曪紒");

      return BigDecimal.ZERO;
    }

    BigDecimal result = num1;
    int j = (arrayOfBigDecimal = num2).length; for (int i = 0; i < j; ++i) { BigDecimal bigDecimal = arrayOfBigDecimal[i];
      if (bigDecimal != null)
        result = result.divide(bigDecimal, scale, roundingMode);

    }

    return result;
  }

  public static final int compare(BigDecimal num1, BigDecimal num2)
  {
    BigDecimal p1 = (num1 == null) ? BigDecimal.ZERO : num1;
    BigDecimal p2 = (num2 == null) ? BigDecimal.ZERO : num2;
    return p1.compareTo(p2);
  }

  public static final Boolean isGreater(BigDecimal num1, BigDecimal num2)
  {
    BigDecimal p1 = (num1 == null) ? BigDecimal.ZERO : num1;
    BigDecimal p2 = (num2 == null) ? BigDecimal.ZERO : num2;

    int result = p1.compareTo(p2);
    return Boolean.valueOf(result == 1);
  }

  public static final Boolean isSmaller(BigDecimal num1, BigDecimal num2)
  {
    BigDecimal p1 = (num1 == null) ? BigDecimal.ZERO : num1;
    BigDecimal p2 = (num2 == null) ? BigDecimal.ZERO : num2;

    int result = p1.compareTo(p2);
    return Boolean.valueOf(result == -1);
  }

  public static final boolean isPositive(BigDecimal[] numArray)
  {
    BigDecimal[] arrayOfBigDecimal;
    int j = (arrayOfBigDecimal = numArray).length; for (int i = 0; i < j; ++i) { BigDecimal num = arrayOfBigDecimal[i];
      if (num == null)
        return false;

      if (num.signum() <= 0)
        return false;
    }

    return true;
  }

  public static final boolean isNegative(BigDecimal[] numArray)
  {
    return ((!(isZero(numArray))) && (!(isPositive(numArray))));
  }

  public static final boolean isZero(BigDecimal[] numArray)
  {
    BigDecimal[] arrayOfBigDecimal;
    int j = (arrayOfBigDecimal = numArray).length; for (int i = 0; i < j; ++i) { BigDecimal num = arrayOfBigDecimal[i];
      if (num == null)
        return false;

      if (num.signum() != 0)
        return false;
    }

    return true;
  }

  public static final boolean isNotZero(BigDecimal[] numArray)
  {
    return (!(isZero(numArray)));
  }

  private static boolean hasZeroOrNull(BigDecimal[] bigDecimals)
  {
    BigDecimal[] arrayOfBigDecimal;
    if (bigDecimals == null)
      return false;

    int j = (arrayOfBigDecimal = bigDecimals).length; for (int i = 0; i < j; ++i) { BigDecimal bigDecimal = arrayOfBigDecimal[i];
      if (bigDecimal != null) if (!(isZero(new BigDecimal[] { bigDecimal }))) continue;
      return true;
    }

    return false;
  }

  public static BigDecimal nvl(BigDecimal data)
  {
    return ((data == null) ? new BigDecimal(0) : data);
  }

  public static BigDecimal nvl(BigDecimal data1, BigDecimal data2)
  {
    return ((data1 == null) ? data2 : data1);
  }

  public static Integer parseInteger(String str)
  {
    return (((str == null) || (str.length() < 1)) ? null : Integer.valueOf(str));
  }

  public static Long parseLong(String str)
  {
    return (((str == null) || (str.length() < 1)) ? null : Long.valueOf(str));
  }

  public static BigDecimal parseBigDecimal(String str)
  {
    return new BigDecimal(str);
  }

  public static String toString(BigDecimal val)
  {
    return ((val == null) ? null : val.toPlainString());
  }

  public static String toString(Double val)
  {
    return ((val == null) ? null : val.toString());
  }

  public static String toString(Integer val)
  {
    return ((val == null) ? null : val.toString());
  }

  public static String toString(Long val)
  {
    return ((val == null) ? null : val.toString());
  }

  public static void main(String[] args) {
    double d = 100.34777D;

    System.out.println(trunc(d, 3));
  }
}
