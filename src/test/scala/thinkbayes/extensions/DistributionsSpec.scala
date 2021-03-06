package thinkbayes.extensions

import org.specs2.mutable.Specification
import thinkbayes._
import thinkbayes.extensions.Distributions._

class DistributionsSpec extends Specification with PmfMatchers {

  "The Distributions extension" should {

    "provide a way to create Pdfs for normal distributions" in {
      todo
    }

    "provide a way to create approximate Pmfs for normal distributions" in {
      forall(-5.0 to 5.0 by 1.0) { mean =>
        forall(0.5 to 7.0 by 0.5) { sd =>
          val pmf = normalPmf(mean, sd, numSigmas = 6).normalized
          pmf.mean must beRelativelyCloseTo(mean)
          pmf.variance must beRelativelyCloseTo(sd * sd)
          pmf.toCdf.value(0.5) must beRelativelyCloseTo(mean)
        }
      }
    }

    "provide a way to create Pmfs for Poisson distributions" in {
      def poissonFunc(lam: Double, k: Int) = math.pow(lam, k) * math.exp(-lam) / (1 to k).product

      poissonPmf(1.5).filterKeys(_ <= 5) must beCloseTo((0 to 5).map { k => (k, poissonFunc(1.5, k)) }.toMap.toPmf)
      foreach(Seq(0.1, 1.0, 3.9)) { lam => poissonPmf(lam).mean must beCloseTo(lam, 0.001) }
    }

    "provide a way to create Pdfs for exponential distributions" in {
      todo
    }

    "provide a way to create approximate Pmfs for exponential distributions" in {
      todo
    }

    "provide a way to create Pmfs for binomial distributions" in {
      binomialPmf(2, 0.6) must beCloseTo(Pmf(0 -> 0.16, 1 -> 0.48, 2 -> 0.36))
      binomialPmf(1, 0.6) must beCloseTo(Pmf(0 -> 0.4, 1 -> 0.6))
      binomialPmf(0, 0.6) must beCloseTo(Pmf(0 -> 1.0))

      binomialPmf(100, 1.0) must beCloseTo(Pmf(100 -> 1.0))
      binomialPmf(100, 0.0) must beCloseTo(Pmf(0 -> 1.0))
    }

    "provide a way to create Pmfs for hypergeometric distributions" in {
      hypergeometricPmf(10, 2, 9) must beCloseTo(Pmf(1 -> 0.2, 2 -> 0.8))
      hypergeometricPmf(10, 2, 2) must beCloseTo(Pmf(0 -> 28.0 / 45, 1 -> 16.0 / 45, 2 -> 1.0 / 45))
      hypergeometricPmf(10, 2, 1) must beCloseTo(Pmf(0 -> 0.8, 1 -> 0.2))
      hypergeometricPmf(10, 2, 0) must beCloseTo(Pmf(0 -> 1.0))

      hypergeometricPmf(100, 100, 100) must beCloseTo(Pmf(100 -> 1.0))
      hypergeometricPmf(100, 100, 50) must beCloseTo(Pmf(50 -> 1.0))
      hypergeometricPmf(100, 100, 49) must beCloseTo(Pmf(49 -> 1.0))
      hypergeometricPmf(100, 100, 0) must beCloseTo(Pmf(0 -> 1.0))
      hypergeometricPmf(100, 50, 100) must beCloseTo(Pmf(50 -> 1.0))
      hypergeometricPmf(100, 0, 50) must beCloseTo(Pmf(0 -> 1.0))
      hypergeometricPmf(100, 0, 1) must beCloseTo(Pmf(0 -> 1.0))
      hypergeometricPmf(100, 0, 0) must beCloseTo(Pmf(0 -> 1.0))
      hypergeometricPmf(0, 0, 0) must beCloseTo(Pmf(0 -> 1.0))
    }

    "provide a way to create bounded Pdfs for beta distributions" in {
      val epsilon = 0.00001

      betaPdf(3.5, 4.7).lowerBound === 0.0
      betaPdf(6.8, 10.1).upperBound === 1.0
      forall(0.0 to 1.0 by 0.1) { p => betaPdf(1.0, 1.0).density(p) === 1.0 }
      forall(0.0 to 1.0 by 0.1) { p => betaPdf(2.0, 1.0).density(p) must beCloseTo(2 * p, epsilon) }
      forall(0.0 to 1.0 by 0.1) { p => betaPdf(1.0, 2.0).density(p) must beCloseTo(2 * (1 - p), epsilon) }
      betaPdf(9.4, 2.3).density(0.45) must beCloseTo(172.285 * math.pow(0.45, 8.4) * math.pow(0.55, 1.3), epsilon)
      betaPdf(9.4, 2.3).density(0.78) must beCloseTo(172.285 * math.pow(0.78, 8.4) * math.pow(0.22, 1.3), epsilon)
    }

    "provide a way to create Pmfs for beta-binomial distributions" in {
      val epsilon = 0.00001

      forall(0 to 100 by 10) { t =>
        betaBinomialPmf(t, 4.56, 7.54).keySet === (0 to t).toSet
        betaBinomialPmf(t, 1.0, 1.0).prob(t) must beCloseTo(1.0 / (t + 1), epsilon)
      }

      forall(1.0 to 5.0 by 0.5) { a =>
        forall(1.0 to 5.0 by 0.5) { b => betaBinomialPmf(1, a, b).prob(0) must beCloseTo(b / (a + b), epsilon) }
      }

      betaBinomialPmf(18, 16.9, 9.4).prob(13) must beCloseTo(0.13846457651739186, epsilon)
      betaBinomialPmf(48, 12.8, 23.7).prob(29) must beCloseTo(0.004926859420972929, epsilon)
      betaBinomialPmf(48, 12.8, 23.7).prob(17) must beCloseTo(0.07817877873188939, epsilon)
      betaBinomialPmf(8, 27.6, 18.1).prob(3) must beCloseTo(0.12456374434810324, epsilon)
    }

    "provide a way to estimate a Pdf from a sequence of samples" in {
      todo
    }
  }
}
