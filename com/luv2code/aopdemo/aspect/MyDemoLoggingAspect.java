package com.luv2code.aopdemo.aspect;

import java.util.List;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.luv2code.aopdemo.Account;

@Aspect
@Component
@Order(2)
public class MyDemoLoggingAspect {
	
	private Logger myLogger = Logger.getLogger(getClass().getName());
	
	@Around("execution(* com.luv2code.aopdemo.service.*.getFortune(..))")
	public Object aroundGetFortune(ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {
		
		// Print out method we are advising on
		String method = theProceedingJoinPoint.getSignature().toShortString();
		myLogger.info("\n=====>> Executing @Around on method: " + method);
		
		// Get starting timestamp
		long begin = System.currentTimeMillis();
		
		// Execute the target metod
		Object result = theProceedingJoinPoint.proceed();
		
		// Get ending timestamp
		long end = System.currentTimeMillis();
		
		// Compute and display the duration
		long duration = end - begin;
		myLogger.info("\n======> Duration: " + duration / 1000.0 + " seconds");
		
		return result;
	}
	
	@After("execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))")
	public void afterFinallyFindAccountsAdvice(JoinPoint theJoinPoint) {
		
		// Print out which method we are advising on
		String method = theJoinPoint.getSignature().toShortString();
		myLogger.info("\n=====>> Executing @Atfer (finally) on method: " + method);
	}
	
	
	@AfterThrowing(
				pointcut="execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
				throwing="theExc")
	public void afterThrowingFindAccountsAdvice(JoinPoint theJoinPoint, Throwable theExc) {
		
		// Print out which method we are advising on
		String method = theJoinPoint.getSignature().toShortString();
		myLogger.info("\n=====>> Executing @AtferThrowing on method: " + method);
		
		// Log the exception
		myLogger.info("\n=====>> The exception is: " + theExc);
	}
	
	// Add a new advice for @AfterReturning on the findAccounts method
	@AfterReturning(
			pointcut="execution(* com.luv2code.aopdemo.dao.AccountDAO.findAccounts(..))",
			returning="result")
	public void afterReturningFindAccountsAdvice(JoinPoint theJoinPoint, List<Account> result) {
		
		// Print out which method is being advised on
		String method = theJoinPoint.getSignature().toShortString();
		myLogger.info("\n=====>> Executing @AtferReturning on method: " + method);
		
		// Print out the results of the method call
		myLogger.info("\n=====>> result is: " + result);
		
		// Convert the account names to all UPPER-CASE
		convertAccountNamesToUpperCase(result);
		myLogger.info("\n======>> result is: " + result);
	}
	
	private void convertAccountNamesToUpperCase(List<Account> result) {
		// Loop through accounts and change names to upper-case
		for(Account tempAccount : result) {
			String upperName = tempAccount.getName().toUpperCase();
			tempAccount.setName(upperName);
		}
	}

	@Before("com.luv2code.aopdemo.aspect.LuvAopExpressions.forDaoPackageNoGetterSetter()")
	public void beforeAddAccountAdvice(JoinPoint theJoinPoint) {
		myLogger.info("\n==========>> Executing @Before advice on method");
		
		// Display the method signature
		MethodSignature methodSig = (MethodSignature) theJoinPoint.getSignature();
		myLogger.info("Method: " + methodSig);
		
		// Display the method arguments
		
		
		//Get arguments
		Object[] args = theJoinPoint.getArgs();
		
		// Loop trough arguments
		for (Object tempArg : args) {
			myLogger.info("Argument: " + tempArg);
			
			if(tempArg instanceof Account) {
				// Cast and print Account specific stuff
				Account theAccount = (Account) tempArg;
				myLogger.info("Account name: " + theAccount.getName());
				myLogger.info("Account level: " + theAccount.getLevel());
			}
		}
	}
}
