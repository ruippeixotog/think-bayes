package thinkbayes.extensions.distributions

import org.apache.commons.math3.distribution.{ IntegerDistribution, RealDistribution }
import thinkbayes.extensions.distributions.CommonsMathConversions._
import thinkbayes._

trait CommonsMathConversions {
  implicit def integerDistributionAsPmf(distrib: IntegerDistribution): Pmf[Int] = IntegerDistributionPmf(distrib)
  implicit def realDistributionAsPdf(distrib: RealDistribution): Pdf = Pdf(distrib.density)
}

object CommonsMathConversions {
  val defaultCutoff = 0.0001

  def approximateIntegerLowerBound(distrib: IntegerDistribution, cutoff: Double = defaultCutoff) =
    if (distrib.getSupportLowerBound != Int.MinValue) distrib.getSupportLowerBound
    else distrib.inverseCumulativeProbability(defaultCutoff)

  def approximateIntegerUpperBound(distrib: IntegerDistribution, cutoff: Double = defaultCutoff) =
    if (distrib.getSupportUpperBound != Int.MaxValue) distrib.getSupportUpperBound
    else distrib.inverseCumulativeProbability(1.0 - defaultCutoff)

  def approximateRealLowerBound(distrib: RealDistribution, cutoff: Double = defaultCutoff) =
    if (!distrib.getSupportLowerBound.isNegInfinity) distrib.getSupportLowerBound
    else distrib.inverseCumulativeProbability(defaultCutoff)

  def approximateRealUpperBound(distrib: RealDistribution, cutoff: Double = defaultCutoff) =
    if (!distrib.getSupportUpperBound.isPosInfinity) distrib.getSupportUpperBound
    else distrib.inverseCumulativeProbability(1.0 - defaultCutoff)

  class IntegerDistributionPmf(distrib: IntegerDistribution, cutoff: Double = defaultCutoff)
      extends Pmf[Int] with ClosedFormPmf[Int] {

    private[this] lazy val lowerBound = approximateIntegerLowerBound(distrib, cutoff)
    private[this] lazy val upperBound = approximateIntegerUpperBound(distrib, cutoff)

    def get(key: Int) = Some(distrib.probability(key))
    def iterator = (lowerBound to upperBound).iterator.map { key => (key, distrib.probability(key)) }
  }

  object IntegerDistributionPmf {
    def apply(distrib: IntegerDistribution, cutoff: Double = defaultCutoff) =
      new IntegerDistributionPmf(distrib, cutoff)
  }

  class RealDistributionPmf(distrib: RealDistribution, domain: Seq[Double])
      extends Pmf[Double] with ClosedFormPmf[Double] {

    def get(key: Double) = Some(distrib.probability(key))
    def iterator = domain.iterator.map { key => (key, distrib.density(key)) }
  }

  object RealDistributionPmf {
    def apply(distrib: RealDistribution, domain: Seq[Double]): RealDistributionPmf =
      new RealDistributionPmf(distrib, domain)

    def apply(distrib: RealDistribution, steps: Int, cutoff: Double = defaultCutoff): RealDistributionPmf = {
      val lowerBound = approximateRealLowerBound(distrib, cutoff)
      val upperBound = approximateRealUpperBound(distrib, cutoff)
      new RealDistributionPmf(distrib, lowerBound to upperBound by ((upperBound - lowerBound) / steps))
    }
  }
}
